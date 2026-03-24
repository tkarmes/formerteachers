package com.formerteachers.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                // 1. SECURE ADMIN PATHS: Only logged-in ADMINs can access /jobs/admin
                .requestMatchers("/jobs/admin/**").hasRole("ADMIN")
                
                // 2. PUBLIC BY DEFAULT: Allow everything else (home, jobs, errors, static resources)
                // This ensures /this-does-not-exist shows a 404 instead of redirecting to login
                .anyRequest().permitAll()
            )
            .formLogin((form) -> form
                .loginPage("/login")
                .defaultSuccessUrl("/jobs/admin", true)
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
