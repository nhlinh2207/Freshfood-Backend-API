package com.linh.freshfoodbackend.controller;


import com.linh.freshfoodbackend.service.ICityService;
import com.linh.freshfoodbackend.service.ICountryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/address")
@AllArgsConstructor
public class AddressController {

    private final ICountryService countryService;
    private final ICityService cityService;

    @GetMapping(path = "/country/getAll")
    public ResponseEntity<?> getAllCountry(){
        return ResponseEntity.ok(countryService.getAll());
    }

    @GetMapping(path = "/city/findByCountry")
    public ResponseEntity<?> getAllCountry(@RequestParam(name = "countryId") Integer countryId){
        return ResponseEntity.ok(cityService.findByCountry(countryId));
    }
}
