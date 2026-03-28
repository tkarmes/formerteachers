package com.formerteachers.config;

import com.formerteachers.model.EmployerProfile;
import com.formerteachers.model.Job;
import com.formerteachers.model.User;
import com.formerteachers.repository.EmployerProfileRepository;
import com.formerteachers.repository.JobRepository;
import com.formerteachers.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, 
                           EmployerProfileRepository employerProfileRepository, 
                           JobRepository jobRepository, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employerProfileRepository = employerProfileRepository;
        this.jobRepository = jobRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create Admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("Admin user created.");
        }

        // Create Sample Employer
        if (userRepository.findByUsername("edtech_inc").isEmpty()) {
            User empUser = new User();
            empUser.setUsername("edtech_inc");
            empUser.setPassword(passwordEncoder.encode("password123"));
            empUser.setRole("EMPLOYER");
            userRepository.save(empUser);

            EmployerProfile profile = new EmployerProfile(empUser, "EdTech Solutions Inc.");
            profile.setCompanyDescription("Leading the way in classroom technology.");
            profile.setWebsite("https://example.com");
            employerProfileRepository.save(profile);

            // Create Sample Jobs
            Job job1 = new Job("Learning Experience Designer", "EdTech Solutions Inc.", "Remote", "$70k - $90k", 
                "We are looking for former teachers to help design engaging digital curriculum.", "EdTech", "Full-time");
            job1.setEmployer(profile);
            job1.setApproved(true);
            job1.setApplyInfo("Apply at jobs@example.com");
            jobRepository.save(job1);

            Job job2 = new Job("Customer Success Manager", "EdTech Solutions Inc.", "New York, NY", "$65k - $85k", 
                "Support school districts in implementing our platform. Classroom experience highly valued!", "Sales/Success", "Full-time");
            job2.setEmployer(profile);
            job2.setApproved(true);
            job2.setApplyInfo("Apply on our website.");
            jobRepository.save(job2);

            System.out.println("Sample employer and jobs created.");
        }
    }
}
