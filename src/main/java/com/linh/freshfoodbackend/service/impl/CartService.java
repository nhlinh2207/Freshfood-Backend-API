package com.linh.freshfoodbackend.service.impl;

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
import com.linh.freshfoodbackend.utils.PaginationCustom;
import com.linh.freshfoodbackend.utils.enums.AddressType;
import com.linh.freshfoodbackend.utils.enums.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
}
