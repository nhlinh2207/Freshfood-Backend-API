package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.MinioInfo;
import com.linh.freshfoodbackend.exception.UnSuccessException;

import java.io.IOException;
import java.io.InputStream;

public interface MinIOService {

    void saveFile(InputStream inputStream, String objectName, String bucketName, String contentType) throws IOException, UnSuccessException;

    void deleteFile(String fileName, String bucketName);

    String getObjectLink(String bucketName, String fileName, Integer expireTime);

    InputStream getObject(MinioInfo info) throws UnSuccessException;

    String getBase64Object(MinioInfo info) throws IOException, UnSuccessException;

}
