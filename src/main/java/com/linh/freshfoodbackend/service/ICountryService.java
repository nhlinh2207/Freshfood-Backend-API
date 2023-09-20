package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.CountryDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;

import java.util.List;

public interface ICountryService {

    ResponseObject<List<CountryDto>> getAll();

}
