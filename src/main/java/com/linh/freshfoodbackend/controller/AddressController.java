package com.linh.freshfoodbackend.controller;


import com.linh.freshfoodbackend.dto.AddressDto;
import com.linh.freshfoodbackend.dto.mapper.MapperUtils;
import com.linh.freshfoodbackend.entity.Address;
import com.linh.freshfoodbackend.service.IAddressService;
import com.linh.freshfoodbackend.service.ICityService;
import com.linh.freshfoodbackend.service.ICountryService;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/address")
@AllArgsConstructor
public class AddressController {

    private final ICountryService countryService;
    private final ICityService cityService;
    private final IAddressService addressService;

    @GetMapping(path = "/country/getAll")
    public ResponseEntity<?> getAllCountry(){
        return ResponseEntity.ok(countryService.getAll());
    }

    @GetMapping(path = "/city/findByCountry")
    public ResponseEntity<?> getAllCountry(@RequestParam(name = "countryId") Integer countryId){
        return ResponseEntity.ok(cityService.findByCountry(countryId));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<String> create(@RequestBody AddressDto addressDto){
         Address address = MapperUtils.map(addressDto, Address.class);
         address.setCreateTime(new Date());
         address.setUpdateTime(new Date());
         address = addressService.create(address);
         return ResponseEntity.ok("Success, Address Id: "+address.getId());
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<String> create(@RequestParam Integer id){
        addressService.delete(id);
        return ResponseEntity.ok("Success, Deleted address by Id: "+id);
    }

    @GetMapping(path = "/findById")
    public ResponseEntity<AddressDto> findById(@RequestParam Integer id) throws NotFoundException {
        Address address = addressService.findById(id);
        return ResponseEntity.ok(MapperUtils.map(address, AddressDto.class));
    }

    @GetMapping(path = "/findAll")
    public ResponseEntity<List<AddressDto>> findAll() {
        List<Address> addresses = addressService.getAll();
        return ResponseEntity.ok(addresses.stream().map(a -> MapperUtils.map(a, AddressDto.class)).collect(Collectors.toList()));
    }

    @PutMapping(path = "/update")
    public ResponseEntity<String> update(@RequestParam Integer id, @RequestBody AddressDto request) throws NotFoundException {
        addressService.update(id, MapperUtils.map(request, Address.class));
        return ResponseEntity.ok("Success, Updated address by Id: "+id);
    }
}
