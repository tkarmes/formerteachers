package com.formerteachers.repository;

import com.formerteachers.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // --- UPDATED: These specific finders are okay, but if you use them publicly,
    // you might want to add 'AndApprovedTrue' to them later.
    // For now, your controller primarily uses searchJobs, which we are fixing below. ---

    Page<Job> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<Job> findByLocationIgnoreCase(String location, Pageable pageable);
    Page<Job> findByCompanyIgnoreCase(String company, Pageable pageable);
    Page<Job> findByWorkTypeIgnoreCase(String workType, Pageable pageable);

    // --- UPDATED: Added "j.approved = true" to the query ---
    @Query("""
        SELECT j FROM Job j
        WHERE
            j.approved = true
            AND (LOWER(j.title) LIKE LOWER(CONCAT('%', COALESCE(:keyword, ''), '%'))
             OR LOWER(j.description) LIKE LOWER(CONCAT('%', COALESCE(:keyword, ''), '%')))
            AND LOWER(j.location) LIKE LOWER(CONCAT('%', COALESCE(:location, ''), '%'))
            AND LOWER(j.category) LIKE LOWER(CONCAT('%', COALESCE(:category, ''), '%'))
            AND LOWER(j.workType) LIKE LOWER(CONCAT('%', COALESCE(:workType, ''), '%'))
        ORDER BY j.createdAt DESC
    """)
    Page<Job> searchJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("category") String category,
            @Param("workType") String workType,
            Pageable pageable
    );

    // ✅ Check for duplicate jobs
    boolean existsByTitleAndCompanyAndLocation(String title, String company, String location);

    // --- UPDATED: Changed method name to include 'ByApprovedTrue' ---
    List<Job> findTop3ByApprovedTrueOrderByCreatedAtDesc();
}