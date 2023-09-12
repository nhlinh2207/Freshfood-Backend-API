package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.request.CreateUserReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.dto.response.user.UserProfile;
import com.linh.freshfoodbackend.entity.Address;
import com.linh.freshfoodbackend.entity.Role;
import com.linh.freshfoodbackend.entity.User;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.IAddressRepo;
import com.linh.freshfoodbackend.repository.IRoleRepo;
import com.linh.freshfoodbackend.repository.IUserRepo;
import com.linh.freshfoodbackend.service.IUserService;
import com.linh.freshfoodbackend.utils.enums.AddressType;
import com.linh.freshfoodbackend.utils.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final IUserRepo userRepo;
    private final IRoleRepo roleRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IAddressRepo addressRepo;

    @Override
    public ResponseObject<String> createUser(CreateUserReq req) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);

            Set<Role> roles = new HashSet<>();
            Role role = roleRepo.findByName("USER");
            roles.add(role);

            Address residentAddress = Address.builder()
                    .countryId(req.getCountryId())
                    .cityId(req.getCityId())
                    .fullAddress(req.getFullAddress())
                    .type(AddressType.RESIDENT)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            User newUser = User.builder()
                    .firstName(req.getFirstName())
                    .lastName(req.getLastName())
                    .email(req.getEmail())
                    .username(req.getUsername())
                    .password(bCryptPasswordEncoder.encode(req.getPassword()))
                    .phoneNumber(req.getPhoneNumber())
                    .isActive(true)
                    .status(UserStatus.ACTIVE)
                    .roles(roles)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            residentAddress.setUser(newUser);
            newUser.setAddress(residentAddress);
            userRepo.save(newUser);

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
            UserProfile profile = UserProfile.builder()
                    .firstName(currentUser.getFirstName())
                    .lastName(currentUser.getLastName())
                    .username(currentUser.getUsername())
                    .email(currentUser.getEmail())
                    .phoneNumber(currentUser.getPhoneNumber())
                    .countryId(address.getCountryId())
                    .cityId(address.getCityId())
                    .fullAddress(address.getFullAddress())
                    .build();
            response.setData(profile);
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
}
