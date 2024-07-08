package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.config.kafka.RatingPublisher;
import com.linh.freshfoodbackend.dto.RankDto;
import com.linh.freshfoodbackend.service.IRatingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping(path = "/rating")
public class RatingController {

    private final IRatingService ratingService;
    private final RatingPublisher ratingPublisher;

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody RankDto req){
//         return ResponseEntity.ok(ratingService.create(req));
         return ResponseEntity.ok(ratingPublisher.publicRatingCreateMessage(req));
    }

    @GetMapping(path = "/getAverageValue")
    public ResponseEntity<?> getAverageValue(@RequestParam Integer productId){
        return ResponseEntity.ok(ratingService.getAverageValueOfProduct(productId));
    }
}
