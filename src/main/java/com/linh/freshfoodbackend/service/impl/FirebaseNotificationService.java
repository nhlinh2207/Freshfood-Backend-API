package com.linh.freshfoodbackend.service.impl;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.linh.freshfoodbackend.dto.request.notification.PushNotificationRequest;
import com.linh.freshfoodbackend.service.IFirebaseNotificationService;
import com.linh.freshfoodbackend.utils.PushNotificationUtil;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class FirebaseNotificationService implements IFirebaseNotificationService {

    @Override
    @Async("SCMExecutor")
    public String pushNotificationToWeb(PushNotificationRequest pushNotification) {
        String message = "";
        try {
            return sendMessageToWeb(pushNotification);
        } catch (InterruptedException | ExecutionException | FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return message;
    }

    private String sendMessageToWeb(PushNotificationRequest request) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        return PushNotificationUtil.pushNotification(request);
    }
}
