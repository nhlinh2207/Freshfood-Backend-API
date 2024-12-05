package com.linh.freshfoodbackend.utils;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class AsyncUtils {

    @Async("SCMExecutor")
    public CompletableFuture<String> test(){
        return CompletableFuture.supplyAsync(() -> {
            try{
                TimeUnit.SECONDS.sleep(3);
                System.out.println(1/0);
            }catch (Exception e){
                e.printStackTrace();
            }
            return "Async task Complete";
        });
    }
}
