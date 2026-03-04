package com.formerteachers.repository;

import com.formerteachers.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    // JpaRepository already gives you save, findAll, findById, deleteById, etc.
}