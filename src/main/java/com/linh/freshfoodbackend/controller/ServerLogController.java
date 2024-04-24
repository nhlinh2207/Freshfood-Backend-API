package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.entity.ServerLog;
import com.linh.freshfoodbackend.repository.IServerLogRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(path = "/log")
@AllArgsConstructor
public class ServerLogController {

    private final IServerLogRepo serverLogRepo;

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody ServerLog req){
        req.setCreateTime(new Date());
        return ResponseEntity.ok(serverLogRepo.saveAndFlush(req));
    }
}
