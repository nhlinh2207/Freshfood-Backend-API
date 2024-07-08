package com.linh.freshfoodbackend.config.kafka;

import com.linh.freshfoodbackend.dto.RankDto;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.service.IRatingService;
import com.linh.freshfoodbackend.utils.Destination;
import com.linh.freshfoodbackend.utils.JsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RatingConsumer {

    private final IRatingService ratingService;
    private final SimpMessagingTemplate webSocketMessagingTemplate;

    @KafkaListener(
            topics = "${spring.kafka.topic.rating_topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenMessage(@Headers MessageHeaders headers, String message) {
        System.out.println("===========> LISTEN <============");
        String header1 = (String)headers.get("LINH");
        String header2= (String)headers.get(KafkaHeaders.RECEIVED_MESSAGE_KEY);
        System.out.println("Header1 : " + header1);
        System.out.println("Header2 : " + header2);
        try{
            // Create rank
            RankDto rankDto = JsonBuilder.parObject(message, RankDto.class);
            ratingService.create(rankDto);
            // Send to websocket client
            webSocketMessagingTemplate.convertAndSend(Destination.totalRanks(), ratingService.getTotalRanks());

            System.out.println("CREATE RATING SUCCESS");
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException("Error Creating Rating : "+e.getMessage());
        }
    }
}
