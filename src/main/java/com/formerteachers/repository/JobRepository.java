package com.formerteachers.repository;

import com.formerteachers.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Paginated filters
    Page<Job> findByCategoryIgnoreCase(String category, Pageable pageable);

    Page<Job> findByLocationIgnoreCase(String location, Pageable pageable);

    Page<Job> findByCompanyIgnoreCase(String company, Pageable pageable);

    // Multi-field search: title, description, or category
    Page<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String title, String description, String category, Pageable pageable);
}