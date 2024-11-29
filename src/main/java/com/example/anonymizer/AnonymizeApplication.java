package com.example.anonymizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.DriverManager;

@SpringBootApplication
@EnableScheduling
public class AnonymizeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnonymizeApplication.class, args);
    }
}
