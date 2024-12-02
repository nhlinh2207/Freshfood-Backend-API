package com.linh.freshfoodbackend.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.linh.freshfoodbackend.dto.MinioInfo;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.service.MinIOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinIOServiceImpl implements MinIOService {

    @Value("${app.storage-minio.expired-time}")
    public int expirationTime;

    private final AmazonS3 s3Client;

    @Override
    public void saveFile(InputStream inputStream, String objectName, String bucketName, String contentType) throws IOException, UnSuccessException {
        try {
            if (!s3Client.doesBucketExistV2(bucketName)) {
                s3Client.createBucket(bucketName);
            }
            byte[] bytes = IOUtils.toByteArray(inputStream);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, byteArrayInputStream, metadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("(saveFile) error: {}", e.getMessage());
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileName, String bucketName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    @Override
    public String getObjectLink(String bucketName, String fileName, Integer expireTime) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(getExpiration(expirationTime));
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    @Override
    public InputStream getObject(MinioInfo info) throws UnSuccessException {
        try {
            ZonedDateTime start = ZonedDateTime.now();
            log.info("(getObject) START: {}", start);
            InputStream object = s3Client.getObject(info.getBucketName(), info.getPath()).getObjectContent();
            ZonedDateTime end = ZonedDateTime.now();
            Long total = ChronoUnit.MILLIS.between(start, end);
            log.info("(getObject) END: {}, TOTAL: {}", end, total);
            return object;
        } catch (Exception e) {
            log.error("(getObject) error: {}", e.getMessage());
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public String getBase64Object(MinioInfo info) throws IOException, UnSuccessException {
        InputStream file = getObject(info);
        byte[] decodedBytes = IOUtils.toByteArray(file);
        return Base64.getEncoder().encodeToString(decodedBytes);
    }

    private Date getExpiration(Integer expirationTime) {
        Date expiration = new Date();
        long expTimeMillis = Instant.now().toEpochMilli();
        expTimeMillis += 1000L * expirationTime;
        expiration.setTime(expTimeMillis);
        log.info("getExpiration {}", expiration);
        return expiration;
    }
}
