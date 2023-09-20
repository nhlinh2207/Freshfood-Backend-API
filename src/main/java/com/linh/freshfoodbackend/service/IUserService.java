package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.request.contact.CreateContactReq;
import com.linh.freshfoodbackend.dto.request.user.CreateUserReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.user.UserProfile;
import com.linh.freshfoodbackend.entity.User;

public interface IUserService {

    ResponseObject<String> createUser(CreateUserReq req);
    ResponseObject<UserProfile> getProfile();
    User getCurrentLoginUser();
    ResponseObject<String> updateProfile(UserProfile profile);
    void createContact(CreateContactReq req, User currentUser);

}
