package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.CityDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.entity.Country;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.ICityRepo;
import com.linh.freshfoodbackend.repository.ICountryRepo;
import com.linh.freshfoodbackend.service.ICityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CityService implements ICityService {

    private final ICityRepo cityRepo;
    private final ICountryRepo countryRepo;

    @Override
    public ResponseObject<List<CityDto>> findByCountry(Integer countryId) {
        try {
            ResponseObject<List<CityDto>> response  = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Country country = countryRepo.findById(countryId).get();
            List<CityDto> data = cityRepo.findByCountry(country).stream().map(
                    c -> CityDto.builder().id(c.getId()).name(c.getName()).build()
            ).collect(Collectors.toList());
            response.setData(data);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }
}
