package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.response.ResponseObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IChartService {

    ResponseObject<Map<String, Integer>> cardChart();
    ResponseObject<Collection<Object>> areaChart();

    ResponseObject<Map<Integer, Object>> pieChart();
}
