package com.linh.freshfoodbackend.utils;

import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linh.freshfoodbackend.dto.request.notification.PushNotificationRequest;

public class PushNotificationUtil {

    public static String pushNotification(PushNotificationRequest request) throws FirebaseMessagingException {
        MulticastMessage message = MulticastMessage.builder()
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setIcon("http://cist.cmc.vn/app-assets/images/logo/favicon.ico")
                                .build()
                        ).build()
                )
                .addAllTokens(request.getTokens()) // web
                .build();

        System.out.println(request.getTokens());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(message);
        String response = FirebaseMessaging.getInstance().sendMulticast(message).getSuccessCount()+" successfully sent";
        // Print response
        System.out.println(response);
        return jsonOutput + "\n" + response;
    }
}
