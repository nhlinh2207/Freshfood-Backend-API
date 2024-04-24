package com.linh.freshfoodbackend.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ContextUtil {

    private String userName;
    private Long userId;
    private String ipRequest;

}
