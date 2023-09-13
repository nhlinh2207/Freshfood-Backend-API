package com.linh.freshfoodbackend.dto.response.textToSpeech;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AudioResp {
    private String audio_link;
    private String idx;
    private String text;
    private Float text_len;
    private Float total;
}
