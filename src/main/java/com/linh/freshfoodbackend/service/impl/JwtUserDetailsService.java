package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.entity.User;
import com.linh.freshfoodbackend.repository.IUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class JwtUserDetailsService extends User implements UserDetailsService {

    private final IUserRepo userRepo;

    public JwtUserDetailsService(IUserRepo userRepo) {
        this.userRepo = userRepo;
    }
    @Override
    @Transactional(readOnly = true)
    public CustomUserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepo.findByEmail(email);
        if (user != null) {
            log.info("login loadUserByEmail : "+ user.getEmail());
            return new CustomUserPrincipal(user, user.getPassword(), user.getIsActive(), true, true, true, this.getAuthority(user));
        } else {
            throw new UsernameNotFoundException("user not found with email = " + email);
        }
    }

    private List<GrantedAuthority> getAuthority(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        log.info("add authorities to UserDetail for email {}", user.getEmail());

        //User có nhiều role
        user.getRoles().forEach(role -> {
            log.info("add role {}", role.getName());
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            //Role co nhieu permission
            role.getPermissions().forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission.getActionCode()));
            });
        });

        //Remove duplicates in arraylist
        Set<GrantedAuthority> set = new HashSet<>(authorities);
        authorities.clear();
        authorities.addAll(set);
        return authorities;
    }
}
