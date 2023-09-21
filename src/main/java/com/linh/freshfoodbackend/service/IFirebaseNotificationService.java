package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.request.notification.PushNotificationRequest;
import com.linh.freshfoodbackend.entity.TokenDevice;
import com.linh.freshfoodbackend.entity.User;

public interface IFirebaseNotificationService {

    String pushNotificationToWeb(PushNotificationRequest pushNotification);
}
