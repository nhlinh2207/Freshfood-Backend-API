package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.config.jwt.JwtTokenUtils;
import com.linh.freshfoodbackend.dto.TokenDeviceDto;
import com.linh.freshfoodbackend.dto.request.contact.CreateContactReq;
import com.linh.freshfoodbackend.dto.request.user.CreateUserReq;
import com.linh.freshfoodbackend.dto.request.user.LoginRequest;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.dto.response.user.UserProfile;
import com.linh.freshfoodbackend.entity.User;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.IUserRepo;
import com.linh.freshfoodbackend.service.ITokenDeviceService;
import com.linh.freshfoodbackend.service.IUserService;
import com.linh.freshfoodbackend.service.impl.CustomUserPrincipal;
import com.linh.freshfoodbackend.service.impl.JwtUserDetailsService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin
@RequestMapping(path = "/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final IUserService userService;
    private final ITokenDeviceService tokenDeviceService;
    private final IUserRepo userRepo;

    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@RequestBody CreateUserReq req){
        log.info("UserController: Thêm mới tài khoản");
        return ResponseEntity.ok(userService.createUser(req));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req){
        log.info("Controller: Tạo token xác thực {}", req.toString());
        try {
            final User user = this.userRepo.findByEmail(req.getEmail());
            if (user == null) {
                log.error("user not found");
                throw new UnSuccessException("User not found : "+req.getEmail());
            }
            this.authenticate(user.getEmail(), req.getPassword());
            final CustomUserPrincipal userDetails = jwtUserDetailsService.loadUserByUsername(user.getEmail());
            final String access_token = jwtTokenUtil.generateToken(userDetails);
            ArrayList<GrantedAuthority> role = new ArrayList<>(userDetails.getAuthorities());
            ResponseObject<?> res = subscriberNull(user, userDetails, access_token, role);
            // Update FCM Token
            tokenDeviceService.update(
                    TokenDeviceDto.builder().webToken(req.getFcmWebToken()).build(),
                    user
            );
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponseObject<>(false, ResponseStatus.UNAUTHORIZE, e.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(path = "/profile")
    public ResponseEntity<?> getProfile(){
        log.info("Get current login user profile");
         return ResponseEntity.ok(userService.getProfile());
    }

    @PutMapping(path = "/getProfile/update")
    public ResponseEntity<?> updateProfile(@RequestBody UserProfile newProfile){
        log.info("Update current login user profile");
        return ResponseEntity.ok(userService.updateProfile(newProfile));
    }

    private void authenticate(String email, String password) {
        log.info("Kiểm tra xác thực {}", email);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    private ResponseObject<?> subscriberNull(User user, CustomUserPrincipal userDetails, String access_token, ArrayList<GrantedAuthority> role) {
        ResponseObject<NullJwt> res = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
        log.info("abc login");
        NullJwt nullJwt = NullJwt.builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .token(access_token)
                .avatar("avatar-s-11.jpg")
                .role(this.roleToArrayList(role))
                .build();
//        log.info(nullJwt.token);
        res.setData(nullJwt);
        return res;
    }

    private ArrayList<String> roleToArrayList(ArrayList<GrantedAuthority> role){
        ArrayList<String> listRole = new ArrayList<>();
        role.forEach(grantedAuthority -> {
            listRole.add(grantedAuthority.getAuthority());
        });
        return listRole;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static public class NullJwt {
        private String username;
        private String fullName;
        private String email;
        private String token;
        private String avatar;
        private ArrayList <String> role;
    }

    @PostMapping(path = "/contact")
    public ResponseEntity<?> createContact(@RequestBody CreateContactReq req){
        try{
            ResponseObject<String>response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            User currentUser = userService.getCurrentLoginUser();
            userService.createContact(req, currentUser);
            response.setData("Success");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            throw new UnSuccessException(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "getAll")
    public ResponseEntity<?> getAllUsers(){
         return ResponseEntity.ok(userService.getAll());
    }
}
