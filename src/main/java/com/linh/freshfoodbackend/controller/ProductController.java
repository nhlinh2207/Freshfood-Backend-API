package com.linh.freshfoodbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/product")
public class ProductController {

    @GetMapping(path = "/all")
    public String getAll(){
        return "okokokok";
    }
}
