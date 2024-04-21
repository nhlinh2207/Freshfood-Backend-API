package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.entity.Address;
import com.linh.freshfoodbackend.repository.BaseRepo;
import com.linh.freshfoodbackend.repository.IAddressRepo;
import com.linh.freshfoodbackend.service.IAddressService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AddressService extends BaseServiceImpl<Address> implements IAddressService {

    private final IAddressRepo addressRepo;

    @Override
    protected BaseRepo<Address> getBaseRepository() {
        return addressRepo;
    }


}
