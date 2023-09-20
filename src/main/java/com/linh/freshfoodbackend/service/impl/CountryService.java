package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.CountryDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.ICityRepo;
import com.linh.freshfoodbackend.repository.ICountryRepo;
import com.linh.freshfoodbackend.service.ICountryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CountryService implements ICountryService {

    private final ICountryRepo countryRepo;

    @Override
    public ResponseObject<List<CountryDto>> getAll() {
        try{
            ResponseObject<List<CountryDto>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            List<CountryDto> data = countryRepo.findAll().stream().map(
                    i -> CountryDto.builder().id(i.getId()).name(i.getName()).build()
            ).collect(Collectors.toList());
            response.setData(data);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }
}
