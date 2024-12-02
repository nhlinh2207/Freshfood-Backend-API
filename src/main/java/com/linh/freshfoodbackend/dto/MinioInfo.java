package com.linh.freshfoodbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MinioInfo {
    private String path;
    private String bucketName;
    private String minioCode;
}
