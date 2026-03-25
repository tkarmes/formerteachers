package com.formerteachers.repository;

import com.formerteachers.model.EmployerProfile;
import com.formerteachers.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {
    Optional<EmployerProfile> findByUser(User user);
}
