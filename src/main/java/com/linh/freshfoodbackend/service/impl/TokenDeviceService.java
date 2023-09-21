package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.TokenDeviceDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.entity.TokenDevice;
import com.linh.freshfoodbackend.entity.User;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.ITokenDeviceRepo;
import com.linh.freshfoodbackend.service.ITokenDeviceService;
import com.linh.freshfoodbackend.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenDeviceService implements ITokenDeviceService {

    private final ITokenDeviceRepo tokenDeviceRepo;
    private final IUserService userService;

    @Override
    public ResponseObject<String> update(TokenDeviceDto tokenDeviceDto, User currentUser) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);

            TokenDevice tokenDevice = currentUser.getTokenDevice();
            tokenDevice.setWebToken(convertToken(tokenDeviceDto.getWebToken()));
            tokenDevice.setIosToken(convertToken(tokenDeviceDto.getIosToken()));
            tokenDevice.setAndroidToken(convertToken(tokenDeviceDto.getAndroidToken()));
            tokenDeviceRepo.saveAndFlush(tokenDevice);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    private String convertToken(String token){
        return (token == null || token.isEmpty()) ? null : token;
    }
}
