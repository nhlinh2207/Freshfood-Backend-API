package com.linh.freshfoodbackend.config.filter;

import com.linh.freshfoodbackend.filter.HospitalFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean(name = "logFilterBean")
    public FilterRegistrationBean<HospitalFilter> loggingFilter(){
        FilterRegistrationBean<HospitalFilter> loggingFilerRegistry
                = new FilterRegistrationBean<>();

        loggingFilerRegistry.setFilter(new HospitalFilter());
        loggingFilerRegistry.addUrlPatterns("/test/*");
        return loggingFilerRegistry;
    }

}
