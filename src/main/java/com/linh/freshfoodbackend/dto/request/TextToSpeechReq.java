package com.linh.freshfoodbackend.dto.request;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TextToSpeechReq {

    private String text;
    private String model;
    private String region;
    private float speed;
    private Boolean text_split;

}
