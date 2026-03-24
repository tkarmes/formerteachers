package com.formerteachers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.formerteachers.model")
@EnableJpaRepositories(basePackages = "com.formerteachers.repository")
public class FormerteachersApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormerteachersApplication.class, args);
    }
}
