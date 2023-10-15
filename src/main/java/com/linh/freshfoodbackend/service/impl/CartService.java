package com.linh.freshfoodbackend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.linh.freshfoodbackend.config.payment.ZaloPayConfig;
import com.linh.freshfoodbackend.dto.CartDto;
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
import com.linh.freshfoodbackend.utils.PaginationCustom;
import com.linh.freshfoodbackend.utils.enums.AddressType;
import com.linh.freshfoodbackend.utils.enums.DateTimeUtil;
import com.linh.freshfoodbackend.utils.enums.OrderStatus;
import com.linh.freshfoodbackend.utils.enums.PaymentType;
import lombok.AllArgsConstructor;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
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
}
