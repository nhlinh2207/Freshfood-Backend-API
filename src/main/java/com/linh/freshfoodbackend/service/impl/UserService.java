package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.mapper.UserMapper;
import com.linh.freshfoodbackend.dto.request.contact.CreateContactReq;
import com.linh.freshfoodbackend.dto.request.notification.PushNotificationRequest;
import com.linh.freshfoodbackend.dto.request.user.CreateUserReq;
import com.linh.freshfoodbackend.dto.response.PaginationResponse;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.dto.response.user.UserProfile;
import com.linh.freshfoodbackend.entity.*;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.IAddressRepo;
import com.linh.freshfoodbackend.repository.IRoleRepo;
import com.linh.freshfoodbackend.repository.IUserRepo;
import com.linh.freshfoodbackend.service.IFirebaseNotificationService;
import com.linh.freshfoodbackend.service.ITokenDeviceService;
import com.linh.freshfoodbackend.service.IUserService;
import com.linh.freshfoodbackend.utils.PaginationCustom;
import com.linh.freshfoodbackend.utils.enums.AddressType;
import com.linh.freshfoodbackend.utils.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final IUserRepo userRepo;
    private final IRoleRepo roleRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IAddressRepo addressRepo;
    private final JavaMailSender mailSender;
    private final IFirebaseNotificationService firebaseNotificationService;
    private final ITokenDeviceService tokenDeviceService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseObject<String> createUser(CreateUserReq req) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Set<Role> roles = new HashSet<>();
            Role role = roleRepo.findByName("USER");
            roles.add(role);

            User newUser = User.builder()
                    .firstName(req.getFirstName())
                    .lastName(req.getLastName())
                    .email(req.getEmail())
                    .username(req.getUsername() == null ? req.getFirstName()+" "+req.getLastName() : req.getUsername())
                    .password(bCryptPasswordEncoder.encode(req.getPassword()))
                    .phoneNumber(req.getPhoneNumber())
                    .isActive(true)
                    .status(UserStatus.ACTIVE)
                    .roles(roles)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            Address residentAddress = Address.builder()
                    .countryId(req.getCountryId())
                    .cityId(req.getCityId())
                    .fullAddress(req.getFullAddress())
                    .type(AddressType.RESIDENT)
                    .user(newUser)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            TokenDevice tokenDevice = TokenDevice.builder()
                            .createTime(new Date())
                            .updateTime(new Date())
                            .user(newUser)
                            .build();

            newUser.setAddress(residentAddress);
            newUser.setTokenDevice(tokenDevice);
            newUser = userRepo.saveAndFlush(newUser);

            // Push notification
            this.firebaseNotificationService.pushNotificationToWeb(PushNotificationRequest.builder()
                    .title("Freshfood")
                    .body(newUser.getEmail()+" đã đăng ký tài khoản tại tại Freshfood")
                    .data("Data: " + newUser.getEmail())
                    .topic("abc")
                    .tokens(Collections.singletonList(tokenDeviceService.findByAdminUser().getWebToken()))
                    .build());

            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<UserProfile> getProfile() {
        try{
            ResponseObject<UserProfile> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            // Get current Login user
            User currentUser = this.getCurrentLoginUser();
            Address address = currentUser.getAddress();
            response.setData(UserMapper.mapEntityToDto(currentUser, address));
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public User getCurrentLoginUser() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByEmail( ((CustomUserPrincipal) authentication.getPrincipal()).getUsername() );
    }

    @Override
    public ResponseObject<String> updateProfile(UserProfile profile) {
        try {
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            User currentUser = this.getCurrentLoginUser();
            // Update User
            currentUser.setFirstName(profile.getFirstName());
            currentUser.setLastName(profile.getLastName());
            currentUser.setUsername(profile.getUsername());
            currentUser.setEmail(profile.getEmail());
            currentUser.setPhoneNumber(profile.getPhoneNumber());
            currentUser.setUpdateTime(new Date());
            userRepo.saveAndFlush(currentUser);
            // Update address
            Address address = currentUser.getAddress();
            address.setCountryId(profile.getCountryId());
            address.setCityId(profile.getCityId());
            address.setFullAddress(profile.getFullAddress());
            address.setUpdateTime(new Date());
            addressRepo.saveAndFlush(address);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    @Async("SCMExecutor")
    public void createContact(CreateContactReq req, User currentUser) {
        try{
            System.out.println(req.getContent());
            // Send mail
            String fullName = currentUser == null ? req.getFullName() : currentUser.getFullName();
            String email = currentUser == null ? req.getEmail() : currentUser.getEmail();
            String content = req.getContent();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true); //nếu dùng multipart thì tham số thứ 2 là true

            //Gửi đến với tên linh
            helper.setFrom(email, fullName);
            helper.setTo("nguyenhoailinh2207@gmail.com");

            String mimeSubject = email+" đã gửi lời nhắn";
            String mimeContent = "<p><b>Tên: <b/><i>"+fullName+"</i></p>";
            mimeContent += "<p><b>Email: </b><i>"+email+"</i></p>";
            mimeContent += content;

            helper.setSubject(mimeSubject);
            //để cấu hình html , tham số thứ 2 là true
            helper.setText(mimeContent, true);

            mailSender.send(mimeMessage);
            System.out.println("send xong");
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public ResponseObject<PaginationResponse<Object>> getAll(Integer page, Integer size, String search, String sortBy, String sortDir, String type, String status) {
        try{
            ResponseObject<PaginationResponse<Object>> res = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Pageable pageable = PaginationCustom.createPaginationCustom(page, size, sortBy, sortDir);

            UserStatus userStatus = UserStatus.getByCode(status);
            Page<User> userPage = userRepo.getAll(search, type, userStatus, pageable);
            List<UserProfile> productData = userPage.getContent().stream().map(
                  u -> UserMapper.mapEntityToDto(u, u.getAddress())
            ).collect(Collectors.toList());
            // Create response
            res.setData(PaginationResponse.builder()
                 .currentPage(userPage.getNumber())
                 .size(userPage.getSize())
                 .totalItems(userPage.getTotalElements())
                 .totalPages(userPage.getTotalPages())
                 .data(productData)
                 .build());
            return res;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public User findById(Integer id) {
        try{
            return userRepo.findById(id).orElseThrow(
                    () -> new UnSuccessException("Can not find User B Id : "+id)
            );
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> delete(Integer id) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            User user = userRepo.findById(id).orElseThrow(
                    () -> new UnSuccessException("Can not find user by id: "+id)
            );
            user.setStatus(UserStatus.DELETED);
            userRepo.saveAndFlush(user);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> changeCurrentPassword(JSONObject req) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            User currentUser = getCurrentLoginUser();
            if (!passwordEncoder.matches(req.get("oldPassword").toString(), currentUser.getPassword())){
                throw new UnSuccessException("Password cũ không chính xác");
            }
            currentUser.setPassword(passwordEncoder.encode(req.get("password").toString()));
            userRepo.saveAndFlush(currentUser);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }
}
