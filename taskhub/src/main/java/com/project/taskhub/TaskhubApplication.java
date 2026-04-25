package com.project.taskhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskhubApplication {

    public static void main(String[] args) {
	SpringApplication.run(TaskhubApplication.class, args);
    }

}
