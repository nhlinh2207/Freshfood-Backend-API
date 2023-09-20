package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.CityDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;

import java.util.List;

public interface ICityService {

    ResponseObject<List<CityDto>> findByCountry(Integer countryId);
}
