//package com.linh.freshfoodbackend.config.filter;
//
//import com.linh.freshfoodbackend.filter.FreshfoodFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FilterConfig {
//
//    @Bean(name = "logFilterBean")
//    public FilterRegistrationBean<FreshfoodFilter> loggingFilter(){
//        FilterRegistrationBean<FreshfoodFilter> loggingFilerRegistry
//                = new FilterRegistrationBean<>();
//
//        loggingFilerRegistry.setFilter(new FreshfoodFilter());
//        loggingFilerRegistry.addUrlPatterns("/test/*");
//        return loggingFilerRegistry;
//    }
//
//}
