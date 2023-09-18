package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.ProductDto;
import com.linh.freshfoodbackend.dto.request.cart.CartItemReq;
import com.linh.freshfoodbackend.dto.request.cart.OrderReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.entity.*;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.IAddressRepo;
import com.linh.freshfoodbackend.repository.ICartItemRepo;
import com.linh.freshfoodbackend.repository.ICartRepo;
import com.linh.freshfoodbackend.repository.IProductRepo;
import com.linh.freshfoodbackend.service.ICartService;
import com.linh.freshfoodbackend.service.IUserService;
import com.linh.freshfoodbackend.utils.enums.AddressType;
import com.linh.freshfoodbackend.utils.enums.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

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
                    .receiverPhoneNumber(req.getPhone())
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
}
