package com.linh.freshfoodbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test")
public class TestController {

    @GetMapping("ok")
    public ResponseEntity<?> test1(){
        return ResponseEntity.ok("okokoko test");
    }

    @GetMapping("bad")
    public ResponseEntity<?> test2(){
        return ResponseEntity.ok("bad test");
    }
}
