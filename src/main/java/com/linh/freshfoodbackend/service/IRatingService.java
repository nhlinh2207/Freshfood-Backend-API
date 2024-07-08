package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.RankDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;

public interface IRatingService {

    ResponseObject<String> create(RankDto req);

    ResponseObject<Integer> getAverageValueOfProduct(Integer productId);

    Integer getTotalRanks();

}
