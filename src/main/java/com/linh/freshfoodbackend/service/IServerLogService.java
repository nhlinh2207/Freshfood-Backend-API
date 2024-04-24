package com.linh.freshfoodbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IServerLogService {
    <E> void createLog(E backupObject, String code, String type, String content, String reason) throws IllegalAccessException, JsonProcessingException;
}
