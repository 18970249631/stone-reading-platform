package com.stonereading;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.stonereading.mapper")
public class StoneReadingApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoneReadingApplication.class, args);
    }
}