package com.linh.freshfoodbackend.utils;

public class Destination {
    public static String publicMessages(String chatRoomId) {
        return "/topic/" + chatRoomId + ".public.messages";
    }

    public static String privateMessages(String chatRoomId) {
        return "/queue/" + chatRoomId + ".private.messages";
    }

    public static String oldMessages(String chatRoomId) {
        return "/queue/" + chatRoomId + ".old.messages";
    }

    public static String connectedUsers(String chatRoomId) {
        return "/topic/" + chatRoomId + ".connected.users";
    }
}
