package com.linh.freshfoodbackend.filter;

import com.linh.freshfoodbackend.utils.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "freshfoodFilter", asyncSupported = true)
@Slf4j
@Component
public class FreshfoodFilter implements Filter {

    @Autowired
    private ContextUtil contextUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String jwtToken = httpRequest.getHeader("Authorization").toString().substring(7);
//        log.info("Hospital filter + "+jwtToken);
//        chain.doFilter(httpRequest, response);

        String ipAddress = request.getRemoteAddr();
        System.out.println("Ip Address : "+ipAddress);
        contextUtil.setIpRequest(ipAddress);
        chain.doFilter(httpRequest, response);
    }
}
