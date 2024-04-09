package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.request.contact.CreateContactReq;
import com.linh.freshfoodbackend.dto.request.user.CreateUserReq;
import com.linh.freshfoodbackend.dto.response.PaginationResponse;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.user.UserProfile;
import com.linh.freshfoodbackend.entity.User;
import org.json.simple.JSONObject;

import java.util.List;

public interface IUserService {

    ResponseObject<String> createUser(CreateUserReq req);
    ResponseObject<UserProfile> getProfile();
    ResponseObject<UserProfile> getProfileById(Integer id);
    User getCurrentLoginUser();
    ResponseObject<String> updateProfile(UserProfile profile);
    ResponseObject<String> updateById(UserProfile profile);
    void createContact(CreateContactReq req, User currentUser);
    User findByEmail(String email);
    ResponseObject<PaginationResponse<Object>> getAll(Integer page, Integer size, String search, String sortBy, String sortDir, String type, String status);
    User findById(Integer id);
    ResponseObject<String> delete(Integer id);
    ResponseObject<String> restore(Integer id);
    ResponseObject<String> changeCurrentPassword(JSONObject req);
    ResponseObject<List<JSONObject>> getStaff();
}
