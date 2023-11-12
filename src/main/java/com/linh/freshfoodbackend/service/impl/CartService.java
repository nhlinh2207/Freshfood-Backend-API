package com.linh.freshfoodbackend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linh.freshfoodbackend.config.payment.ZaloPayConfig;
import com.linh.freshfoodbackend.dto.CartDto;
import com.linh.freshfoodbackend.dto.JasperDto;
import com.linh.freshfoodbackend.dto.mapper.CartMapper;
import com.linh.freshfoodbackend.dto.request.cart.CartItemReq;
import com.linh.freshfoodbackend.dto.request.cart.OrderReq;
import com.linh.freshfoodbackend.dto.response.PaginationResponse;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.entity.*;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.ICartItemRepo;
import com.linh.freshfoodbackend.repository.ICartRepo;
import com.linh.freshfoodbackend.repository.IProductRepo;
import com.linh.freshfoodbackend.service.ICartService;
import com.linh.freshfoodbackend.service.IUserService;
import com.linh.freshfoodbackend.utils.HMACUtil;
import com.linh.freshfoodbackend.utils.MoneyFormatUtil;
import com.linh.freshfoodbackend.utils.PaginationCustom;
import com.linh.freshfoodbackend.utils.enums.AddressType;
import com.linh.freshfoodbackend.utils.enums.DateTimeUtil;
import com.linh.freshfoodbackend.utils.enums.OrderStatus;
import com.linh.freshfoodbackend.utils.enums.PaymentType;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartService implements ICartService {

    private final ICartRepo cartRepo;
    private final ICartItemRepo cartItemRepo;
    private final IUserService userService;
    private final IProductRepo productRepo;

    @Override
    public ResponseObject<String> createOrder(OrderReq req) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            User currentUser = userService.getCurrentLoginUser();

            // Create delivery address
            Address deliveryAddress = Address.builder()
                    .countryId(req.getCountryId())
                    .cityId(req.getCityId())
                    .fullAddress(req.getFullAddress())
                    .type(AddressType.DELIVERY)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            // Save Order
            Cart cart = Cart.builder()
                    .address(deliveryAddress)
                    .orderTime(new Date())
                    .status(OrderStatus.UNSENT)
                    .paymentType(PaymentType.OFFLINE)
                    .isPaid(false)
                    .receiverEmail(req.getEmail())
                    .receiverName(req.getFullName())
                    .totalPrice(req.getCartItems().stream().mapToInt(CartItemReq::getSum).sum())
                    .receiverPhoneNumber(req.getPhoneNumber())
                    .user(currentUser)
                    .build();

            deliveryAddress.setCart(cart);
            cart = cartRepo.saveAndFlush(cart);

            // Save cartItem
            for (CartItemReq item : req.getCartItems()){
                Product product =  productRepo.findById(item.getId()).get();
                CartItem cartItem = cartItemRepo.saveAndFlush(CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(item.getQty())
                        .totalPrice(item.getSum())
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build());
            }

            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<PaginationResponse<Object>> findByUser(Integer page, String fromOrderTime, String toOrderTime) {
        try{
            SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date from = fromOrderTime.isEmpty() ? smf.parse("2020-01-01 00:00:00") : smf.parse(fromOrderTime+" 00:00:00");
            Date to = toOrderTime.isEmpty() ? new Date() : smf.parse(toOrderTime+" 23:59:59");
            Pageable pageable = PaginationCustom.createPaginationCustom(page, 10, "orderTime", "desc");
            ResponseObject<PaginationResponse<Object>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            User currentUser = userService.getCurrentLoginUser();
            Page<Cart> cartPage = cartRepo.findByUser(currentUser, from, to, pageable);
            List<CartDto> carts = cartPage.getContent().stream()
                    .map(CartMapper::mapToCartDto).collect(Collectors.toList());
            response.setData(PaginationResponse.builder()
                    .currentPage(cartPage.getNumber())
                    .size(cartPage.getSize())
                    .totalItems(cartPage.getTotalElements())
                    .totalPages(cartPage.getTotalPages())
                    .data(carts)
                    .build());
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<PaginationResponse<Object>> findByStaff(Integer page, String fromOrderTime, String toOrderTime) {
        try{
            SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date from = fromOrderTime.isEmpty() ? smf.parse("2020-01-01 00:00:00") : smf.parse(fromOrderTime+" 00:00:00");
            Date to = toOrderTime.isEmpty() ? new Date() : smf.parse(toOrderTime+" 23:59:59");
            Pageable pageable = PaginationCustom.createPaginationCustom(page, 10, "orderTime", "desc");
            ResponseObject<PaginationResponse<Object>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            User staff = userService.getCurrentLoginUser();
            Page<Cart> cartPage = cartRepo.findByStaff(staff, from, to, pageable);
            List<CartDto> carts = cartPage.getContent().stream()
                    .map(CartMapper::mapToCartDto).collect(Collectors.toList());
            response.setData(PaginationResponse.builder()
                    .currentPage(cartPage.getNumber())
                    .size(cartPage.getSize())
                    .totalItems(cartPage.getTotalElements())
                    .totalPages(cartPage.getTotalPages())
                    .data(carts)
                    .build());
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> delete(Integer cartId) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Cart cart = cartRepo.findById(cartId).orElseThrow(
                    () -> new UnSuccessException("Can not find cart bt id : "+cartId)
            );
            cart.setStatus(OrderStatus.DELETED);
            cartRepo.saveAndFlush(cart);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<CartDto> findById(Integer cartId) {
        try{
            ResponseObject<CartDto> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Cart cart = cartRepo.findById(cartId).orElseThrow(
                    () -> new UnSuccessException("Can not find cart bt id : "+cartId)
            );
            response.setData(CartMapper.mapToCartDto(cart));
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<PaginationResponse<Object>> findAll(Integer page, String fromOrderTime, String toOrderTime, String status) {
        try{
            SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date from = fromOrderTime.isEmpty() ? smf.parse("2020-01-01 00:00:00") : smf.parse(fromOrderTime+" 00:00:00");
            Date to = toOrderTime.isEmpty() ? new Date() : smf.parse(toOrderTime+" 23:59:59");
            Pageable pageable = PaginationCustom.createPaginationCustom(page, 10, "orderTime", "desc");
            ResponseObject<PaginationResponse<Object>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            OrderStatus orderStatus = OrderStatus.getByCode(status);
            Page<Cart> cartPage = cartRepo.findAll(from, to, orderStatus, pageable);

            List<CartDto> carts = cartPage.getContent().stream()
                    .map(CartMapper::mapToCartDto).collect(Collectors.toList());
            response.setData(PaginationResponse.builder()
                    .currentPage(cartPage.getNumber())
                    .size(cartPage.getSize())
                    .totalItems(cartPage.getTotalElements())
                    .totalPages(cartPage.getTotalPages())
                    .data(carts)
                    .build());
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> createZaloPay(OrderReq req) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            User currentUser = userService.getCurrentLoginUser();
            ObjectMapper objectMapper = new ObjectMapper();

            // Create delivery address
            Address deliveryAddress = Address.builder()
                    .countryId(req.getCountryId())
                    .cityId(req.getCityId())
                    .fullAddress(req.getFullAddress())
                    .type(AddressType.DELIVERY)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            // Save Order
            Cart cart = Cart.builder()
                    .address(deliveryAddress)
                    .orderTime(new Date())
                    .status(OrderStatus.UNSENT)
                    .receiverEmail(req.getEmail())
                    .receiverName(req.getFullName())
                    .paymentType(PaymentType.ZALOPAY)
                    .isPaid(false)
                    .totalPrice(req.getCartItems().stream().mapToInt(CartItemReq::getSum).sum())
                    .receiverPhoneNumber(req.getPhoneNumber())
                    .user(currentUser)
                    .build();

            deliveryAddress.setCart(cart);
            cart = cartRepo.saveAndFlush(cart);

            // Save cartItem
            for (CartItemReq item : req.getCartItems()){
                Product product =  productRepo.findById(item.getId()).get();
                CartItem cartItem = cartItemRepo.saveAndFlush(CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(item.getQty())
                        .totalPrice(item.getSum())
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build());
            }

            // Send request to Zalopay to create QR
            Map<String, Object> zalopay_Params = new HashMap<>();
            zalopay_Params.put("appid", ZaloPayConfig.APP_ID);
            zalopay_Params.put("apptransid", DateTimeUtil.getCurrentTimeString("yyMMdd") + "_" + new Date().getTime());
            zalopay_Params.put("apptime", System.currentTimeMillis());
            zalopay_Params.put("appuser", currentUser.getUsername() == null ? currentUser.getEmail() : currentUser.getUsername());
            zalopay_Params.put("amount", req.getCartItems().stream().mapToInt(CartItemReq::getSum).sum());
            zalopay_Params.put("description", "Thanh toan don hang #" + cart.getId());
            zalopay_Params.put("bankcode", "");
            String item = objectMapper.writeValueAsString(req.getCartItems());;
            zalopay_Params.put("item", Normalizer.normalize(item, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));

            // embeddata
            // Trong trường hợp Merchant muốn trang cổng thanh toán chỉ hiện thị danh sách
            // các ngân hàng ATM,
            // thì Merchant để bankcode="" và thêm bankgroup = ATM vào embeddata như ví dụ
            // bên dưới
            // embeddata={"bankgroup": "ATM"}
            // bankcode=""

            Map<String, String> embeddata = new HashMap<>();
            embeddata.put("merchantinfo", "FreshfoodShop");
            embeddata.put("promotioninfo", "");
            embeddata.put("redirecturl", ZaloPayConfig.REDIRECT_URL);

            Map<String, String> columninfo = new HashMap<String, String>();
            columninfo.put("store_name", "Freshfood");
            embeddata.put("columninfo", new JSONObject(columninfo).toString());
            zalopay_Params.put("embeddata", new JSONObject(embeddata).toString());

            String data = zalopay_Params.get("appid") + "|" + zalopay_Params.get("apptransid") + "|"
                    + zalopay_Params.get("appuser") + "|" + zalopay_Params.get("amount") + "|"
                    + zalopay_Params.get("apptime") + "|" + zalopay_Params.get("embeddata") + "|"
                    + zalopay_Params.get("item");
            zalopay_Params.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, data));
		    // zalopay_Params.put("phone", order.getPhone());
		    // zalopay_Params.put("email", order.getEmail());
		    // zalopay_Params.put("address", order.getAddress());
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(ZaloPayConfig.CREATE_ORDER_URL);

            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, Object> e : zalopay_Params.entrySet()) {
                params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
            }
            post.setEntity(new UrlEncodedFormEntity(params));
            CloseableHttpResponse res = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder resultJsonStr = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                resultJsonStr.append(line);
            }
            JSONObject result = new JSONObject(resultJsonStr.toString());
            Map<String, Object> kq = new HashMap<String, Object>();
            kq.put("returnmessage", result.get("returnmessage"));
            kq.put("orderurl", result.get("orderurl"));
            kq.put("returncode", result.get("returncode"));
            kq.put("zptranstoken", result.get("zptranstoken"));
            response.setData(kq.get("orderurl").toString());
            System.out.println(kq);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> assignToStaff(Integer cartId, Integer staffId) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Cart cart = cartRepo.findById(cartId).orElseThrow(
                    () -> new UnSuccessException("Can not find cart with id : "+cartId)
            );
            User staff = userService.findById(staffId);
            cart.setStaff(staff);
            cart.setStatus(OrderStatus.PENDING);
            cartRepo.saveAndFlush(cart);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> deliveryCart(Integer cartId) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Cart cart = cartRepo.findById(cartId).orElseThrow(
                    () -> new UnSuccessException("Can not find cart with id : "+cartId)
            );
            cart.setIsDelivered(true);
            cart.setDeliveryTime(new Date());
            cart.setStatus(OrderStatus.DELIVERED);
            cartRepo.saveAndFlush(cart);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> receiveCart(Integer cartId) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Cart cart = cartRepo.findById(cartId).orElseThrow(
                    () -> new UnSuccessException("Can not find cart with id : "+cartId)
            );
            cart.setIsReceived(true);
            cart.setStatus(OrderStatus.COMPLETE);
            cartRepo.saveAndFlush(cart);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public void exportFile(Integer cartId, HttpServletResponse response) {
        try{
            SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String logoImage = "";
            Cart cart = cartRepo.findById(cartId).orElseThrow(
                    () -> new UnSuccessException("Can not find cart by id: "+cartId)
            );
//            List<org.json.simple.JSONObject> cartItems = cart.getCartItems().stream().map(
//                    c -> {
//                        org.json.simple.JSONObject j = new org.json.simple.JSONObject();
//                        j.put("id", c.getId());
//                        j.put("productName", c.getProduct().getName());
//                        j.put("image", c.getProduct().getExtra_img1().replaceFirst("data:image/jpeg;base64,", ""));
//                        j.put("itemTotalPrice", MoneyFormatUtil.format(c.getTotalPrice()));
//                        j.put("quantity", c.getQuantity());
//                        j.put("price", MoneyFormatUtil.format(c.getProduct().getPrice()));
//                        return j;
//                    }
//            ).collect(Collectors.toList());

            List<JasperDto> cartItems = cart.getCartItems().stream().map(
                    c -> JasperDto.builder()
                            .productName(c.getProduct().getName())
                            .productId(c.getProduct().getId())
                            .image(c.getProduct().getExtra_img1().replaceFirst("data:image/jpeg;base64,", ""))
                            .price(MoneyFormatUtil.format(c.getProduct().getPrice()))
                            .quantity(c.getQuantity())
                            .itemTotalPrice(MoneyFormatUtil.format(c.getTotalPrice()))
                            .build()
            ).collect(Collectors.toList());

            User customer = cart.getUser();
            User staff = cart.getStaff();

            Map<String, Object> params = new HashMap<>();
            params.put("logo", "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");
            params.put("cartId", cartId);
            params.put("name", customer.getFirstName()+" "+customer.getLastName());
            params.put("phone", cart.getReceiverPhoneNumber() == null ? customer.getPhoneNumber() : cart.getReceiverPhoneNumber());
            params.put("email", cart.getReceiverEmail() == null ? customer.getEmail() : cart.getReceiverEmail());
            params.put("address", cart.getAddress().getFullAddress());
            params.put("orderTime", smf.format(cart.getOrderTime()));
            params.put("totalPrice", MoneyFormatUtil.format(cart.getTotalPrice()));
            params.put("staffName", staff.getFirstName()+" "+staff.getLastName());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=freshfood_hoa_don_mua_hang.pdf");

            File reportFile = new ClassPathResource("static/report/cartReport.jasper").getFile();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(cartItems);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

//            Export pdf
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            exporter.exportReport();

        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }
}
