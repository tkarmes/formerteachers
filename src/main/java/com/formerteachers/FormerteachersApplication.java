package com.formerteachers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.formerteachers.model.Job;
import com.formerteachers.repository.JobRepository;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication

public class FormerteachersApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormerteachersApplication.class, args);
    }



}