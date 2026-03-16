package com.mahedee.taskmagnet.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mahedee.taskmagnet")
public class TaskMagnetApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskMagnetApplication.class, args);
    }
}
