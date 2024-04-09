package com.linh.freshfoodbackend.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "hospitalFilter", asyncSupported = true)
@Slf4j
@Component
public class HospitalFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String jwtToken = httpRequest.getHeader("Authorization").toString().substring(7);
        log.info("Hospital filter + "+jwtToken);
        chain.doFilter(httpRequest, response);
    }
}
