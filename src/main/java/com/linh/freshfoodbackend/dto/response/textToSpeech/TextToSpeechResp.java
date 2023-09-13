package com.linh.freshfoodbackend.dto.response.textToSpeech;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TextToSpeechResp {
    private String ai_version;
    private String code;
    private List<AudioResp> playlist;
    private String text_id;
    private String text_length;
    private String version;
}
