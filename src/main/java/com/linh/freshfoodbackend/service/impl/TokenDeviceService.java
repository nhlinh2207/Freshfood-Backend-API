package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.TokenDeviceDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.entity.TokenDevice;
import com.linh.freshfoodbackend.entity.User;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.ITokenDeviceRepo;
import com.linh.freshfoodbackend.repository.IUserRepo;
import com.linh.freshfoodbackend.service.ITokenDeviceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenDeviceService implements ITokenDeviceService {

    private final ITokenDeviceRepo tokenDeviceRepo;
    private final IUserRepo userRepo;

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

    @Override
    public TokenDevice findByUser(User user) {
        return tokenDeviceRepo.findByUser(user);
    }

    @Override
    public TokenDevice findByAdminUser() {
        User admin = userRepo.findByEmail("nguyenhoailinh2207@gmail.com");
        return tokenDeviceRepo.findByUser(admin);
    }

    private String convertToken(String token){
        return (token == null || token.isEmpty()) ? null : token;
    }
}
