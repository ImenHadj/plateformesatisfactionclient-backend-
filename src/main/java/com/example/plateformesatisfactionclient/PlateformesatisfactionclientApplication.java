package com.example.plateformesatisfactionclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling  // Active la planification des tâches

@SpringBootApplication
public class PlateformesatisfactionclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlateformesatisfactionclientApplication.class, args);
    }

}
