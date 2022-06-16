package com.vmo.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedisExample implements CommandLineRunner {
    @Autowired
    private RedisTemplate template;

    @Autowired
    CacheManager cacheManager;


    @Override
    public void run(String... args) throws Exception {
        listExample();
    }

    public void valueExample(){
        // Set giá trị của key "loda" là "hello redis"
        template.opsForValue().set("loda","hello world");

        // In ra màn hình Giá trị của key "loda" trong Redis
        System.out.println("Value of key loda: "+template.opsForValue().get("loda"));
    }

    public void listExample(){
        // Tạo ra một list gồm 2 phần tử
        List<String> list = new ArrayList<>();
//        list.add("Hello");
//        list.add("redis");
        // Set gia trị có key loda_list
//        template.opsForList().rightPushAll("loda_list", list);
//        template.opsForList().rightPushAll("loda_list", "hello", "world");
        System.out.println("Size of key loda: "+template.opsForList().size("loda_list"));
        System.out.println("value 1st: " + template.opsForList().index("loda_list", 1));
    }
}
