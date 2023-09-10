package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.request.CreateUserReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;

public interface IUserService {

    ResponseObject<String> createUser(CreateUserReq req);

}
