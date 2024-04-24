package com.linh.freshfoodbackend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linh.freshfoodbackend.entity.Address;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.BaseRepo;
import com.linh.freshfoodbackend.repository.IAddressRepo;
import com.linh.freshfoodbackend.repository.IServerLogRepo;
import com.linh.freshfoodbackend.service.IAddressService;
import com.linh.freshfoodbackend.service.IServerLogService;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AddressService extends BaseServiceImpl<Address> implements IAddressService {

    private final IServerLogService serverLogService;
    private final IServerLogRepo serverLogRepo;
    private final IAddressRepo addressRepo;

    @Override
    protected BaseRepo<Address> getBaseRepository() {
        return addressRepo;
    }

    @Override
    public void update(Integer id, Address address) throws NotFoundException, JsonProcessingException, IllegalAccessException {
        // Lưu log
        Address oldValue = addressRepo.findById(id).orElseThrow(
                () -> new UnSuccessException("Not found Address By Id : "+id)
        );
        serverLogService.createLog(oldValue, "UPDATE", "ADDRESS", "Cập nhật thông tin ADDRESS", "Cập nhật thông tin ADDRESS");
        super.update(id, address);
    }
}
