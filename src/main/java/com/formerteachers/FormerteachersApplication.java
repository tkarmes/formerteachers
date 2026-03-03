package com.formerteachers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FormerteachersApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormerteachersApplication.class, args);
    }


    @Bean
    CommandLineRunner initData(JobRepository jobRepository) {
        return args -> {
            if (jobRepository.count() == 0) {
                jobRepository.save(new Job(
                        "Instructional Designer - Math Focus",
                        "McGraw Hill",
                        "Remote",
                        "$55k–$64k",
                        "Create math assessments and content for K-12."
                ));

                jobRepository.save(new Job(
                        "Curriculum Specialist (K-12 Math)",
                        "Savvas Learning",
                        "Hybrid Denver",
                        "$37–$40/hr part-time",
                        "Develop K-12 math curriculum materials."
                ));

                jobRepository.save(new Job(
                        "Assessment Content Creator - Middle School Math",
                        "Khan Academy",
                        "Remote",
                        "$77k–$87k fixed-term",
                        "Create high-quality middle school math assessments."
                ));

                System.out.println("Sample jobs seeded into DB!");
            }
        };
    }

}