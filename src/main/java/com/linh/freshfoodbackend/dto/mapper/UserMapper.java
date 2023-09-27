package com.linh.freshfoodbackend.dto.mapper;

import com.linh.freshfoodbackend.dto.response.user.UserProfile;
import com.linh.freshfoodbackend.entity.Address;
import com.linh.freshfoodbackend.entity.User;

public class UserMapper {

    public static UserProfile mapEntityToDto(User currentUser, Address address){
        return UserProfile.builder()
                .id(currentUser.getId())
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .phoneNumber(currentUser.getPhoneNumber())
                .countryId(address.getCountryId())
                .cityId(address.getCityId())
                .fullAddress(address.getFullAddress())
                .build();
    }
}
