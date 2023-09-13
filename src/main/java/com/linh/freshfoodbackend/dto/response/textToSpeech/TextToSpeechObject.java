package com.linh.freshfoodbackend.dto.response.textToSpeech;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TextToSpeechObject {

    private String message;
    private TextToSpeechResp object;
    private String hash_text;
    private Integer length_text;

}
