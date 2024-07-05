package com.linh.freshfoodbackend.config.jwt;

import com.google.common.net.HttpHeaders;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.service.impl.CustomUserPrincipal;
import com.linh.freshfoodbackend.service.impl.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.models.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtils jwtTokenUtils;

    public JwtRequestFilter(JwtUserDetailsService jwtUserDetailsService, JwtTokenUtils jwtTokenUtils) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtils = jwtTokenUtils;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With, No-Auth");
        response.setHeader("Content-Type", "application/json");
        if (request.getHeader(HttpHeaders.ORIGIN) != null
                && request.getMethod().equals(HttpMethod.OPTIONS.name())
                && request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null) {
            log.debug("Received an OPTIONS pre-flight request.");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtils.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                throw new UnSuccessException("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                throw new UnSuccessException("JWT Token has expired");
            }
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserPrincipal userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            // if token is valid configure Spring Security to manually set authentication
            if (jwtTokenUtils.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
