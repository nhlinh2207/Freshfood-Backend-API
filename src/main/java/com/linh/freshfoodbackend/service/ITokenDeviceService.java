package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.TokenDeviceDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.entity.User;

public interface ITokenDeviceService {

    ResponseObject<String> update(TokenDeviceDto tokenDeviceDto, User currentUser);
}
