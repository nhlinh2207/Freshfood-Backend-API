package com.linh.freshfoodbackend;

import com.linh.freshfoodbackend.utils.AsyncUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class FreshfoodBackendApplication implements CommandLineRunner {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AsyncUtils asyncUtils;

    public static void main(String[] args) {
        SpringApplication.run(FreshfoodBackendApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
//        JSONObject ob1 = new JSONObject();
//        ob1.put("id", 1);
//        ob1.put("name", "Linh");
//        redisTemplate.opsForHash().put("user", 1, ob1);
//
//        ob1 = new JSONObject();
//        ob1.put("id", 2);
//        ob1.put("name", "lan");
//        redisTemplate.opsForHash().put("user", 2, ob1);
//
//        // Get all
//        System.out.println(redisTemplate.opsForHash().values("user"));
//
//        // Get By id
//        System.out.println(redisTemplate.opsForHash().get("user", 1));
//
//        // Delete By Id
//        redisTemplate.opsForHash().delete("user", 1);
//
//        //  Print Again
//        System.out.println(redisTemplate.opsForHash().values("user"));

        // Test Async
//        asyncUtils.test().thenAccept(System.out::println);
//        System.out.println("Main task complete");
    }
}
