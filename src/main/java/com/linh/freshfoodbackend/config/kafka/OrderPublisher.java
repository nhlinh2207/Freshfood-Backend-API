package com.linh.freshfoodbackend.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linh.freshfoodbackend.dto.request.cart.OrderReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.service.IUserService;
import com.linh.freshfoodbackend.utils.JsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderPublisher {

    @Value(value = "${spring.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final IUserService userService;

    public ResponseObject<String> publishOrderMessage(OrderReq req){
        try{
            req.setCurrentUserEmail(userService.getCurrentLoginUser().getEmail());
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            String[] result = {"Success"};
            String payload = JsonBuilder.parseString(req);
            Message<String> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.MESSAGE_KEY, "LINH_MESSAGE_KEY")
                    .setHeader("LINH", "LINH_TEST_HEADER")
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .build();

            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
            // Success case
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    System.out.println("===========> PUBLISH SUCCESS <============");
                    System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
                }

                @Override
                public void onFailure(Throwable ex) {
                    System.out.println("===========> PUBLISH ERROR <============");
                    System.err.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
                    result[0] = "Fail";
                }
            });
            System.out.println("BEFORE FUTURE !!!!!!");
            response.setData( result[0]);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException("Error publish order request : "+e.getMessage());
        }
    }
}
