package com.linh.freshfoodbackend.dto.request.notification;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PushNotificationRequest {
    private String title;
    private String body;
    private String data;
    private String topic;
    private List<String> tokens;
    private String image;
}
