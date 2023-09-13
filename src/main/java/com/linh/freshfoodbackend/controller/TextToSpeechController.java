package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.dto.request.TextToSpeechReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.dto.response.textToSpeech.AudioResp;
import com.linh.freshfoodbackend.dto.response.textToSpeech.TextToSpeechObject;
import com.linh.freshfoodbackend.dto.response.textToSpeech.TextToSpeechResp;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/tts")
public class TextToSpeechController {

    @GetMapping(path = "/test")
    public ResponseEntity<?> test(){
        try{
            ResponseObject<List<String>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            headers.add("Authorization", "bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIwNTIxYWRiZC1kOGU4LTZmNmEtZTA2My02MjE5OWYwYWM4Y2IiLCJhdWQiOlsicmVzdHNlcnZpY2UiXSwidXNlcl9uYW1lIjoibmd1eWVuaG9haWxpbmhAdm5wdC52biIsInNjb3BlIjpbInJlYWQiXSwiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3QiLCJuYW1lIjoibmd1eWVuaG9haWxpbmhAdm5wdC52biIsInV1aWRfYWNjb3VudCI6IjA1MjFhZGJkLWQ4ZTgtNmY2YS1lMDYzLTYyMTk5ZjBhYzhjYiIsImF1dGhvcml0aWVzIjpbIlVTRVIiXSwianRpIjoiOTgyZjAzOTQtZjQ1Zi00YWMyLWE3NjYtYTE3YWE4NWQ1NDkwIiwiY2xpZW50X2lkIjoiYWRtaW5hcHAifQ.0iEPDE-8Atie5ODkKzYBSa2WFG3EEAHNcg4PG2MVFFlDfCsI188chHTu6oN-0mktGDSaq3Ko0NKAAtxKZVXlnGyWq7dEbFMDAOoB-lU0vf_Tda21BpEgFkIKYxgqyWPcmS6Jyz-mTUHgsMIr2wGQtXUHBldrCaRSl5rZ-V4ITrPi57s4E_bNvESH5P5oKgX-W-z6xer_-EuoYInUaT0M7CvDH6JPs3OYY--Fxuanp3Ot3NfguMlftynsql8wS8FdWZDAKlw6m-8cY5KhidH8GCOVdFnPLwQ6y_oRchaXb84pNisYcpx48CVIusMa3p0Pko93rQw4tQOLZ5u1LJ3w3A");
            headers.add("Token-id", "0521b04d-cdcd-67c8-e063-62199f0a95e5");
            headers.add("Token-key", "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALAki4E0CJSiEMSbAzYBvRCWRlwN05aHlQHyNvpgX91LPgHjQt678Sady+15snNd5I1kDe6jy0UnyRhxVSUbafUCAwEAAQ==");

            TextToSpeechReq req = TextToSpeechReq.builder()
                    .text("I can do everything")
                    .model("news")
                    .region("female_north")
                    .speed(1.0f)
                    .text_split(false)
                    .build();

            HttpEntity<TextToSpeechReq> requestEntity = new HttpEntity<>(req, headers);
            ParameterizedTypeReference<TextToSpeechObject> responseType = new ParameterizedTypeReference<TextToSpeechObject>() {};
            ResponseEntity<TextToSpeechObject> responseEntity = restTemplate.exchange("https://api.idg.vnpt.vn/tts-service/v2/grpc", HttpMethod.POST, requestEntity, responseType);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)){
                TextToSpeechObject responseObject = responseEntity.getBody();
                TextToSpeechResp data = responseObject.getObject();
                List<AudioResp> audioData = data.getPlaylist();
                response.setData(
                        audioData.stream().map(i -> i.getAudio_link()).collect(Collectors.toList())
                );
            }
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }
}
