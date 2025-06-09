package com.example.springcode5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // 启用定时任务
@EnableAsync      // 启用异步任务
public class SpringCode5Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringCode5Application.class, args);
    }
}