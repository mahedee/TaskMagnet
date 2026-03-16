package com.mahedee.taskmagnet.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.mahedee.taskmagnet")
@EntityScan(basePackages = "com.mahedee.taskmagnet.domain.model")
@EnableJpaRepositories(basePackages = "com.mahedee.taskmagnet.domain.repository")
public class TaskMagnetApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskMagnetApplication.class, args);
    }
}
