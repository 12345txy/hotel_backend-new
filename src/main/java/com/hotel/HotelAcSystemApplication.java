package com.hotel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.hotel.mapper")
@EnableScheduling
public class HotelAcSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelAcSystemApplication.class, args);
    }

} 