package com.formerteachers.repository;

import com.formerteachers.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Paginated filters
    Page<Job> findByCategoryIgnoreCase(String category, Pageable pageable);

    Page<Job> findByLocationIgnoreCase(String location, Pageable pageable);

    Page<Job> findByCompanyIgnoreCase(String company, Pageable pageable);

    Page<Job> findByWorkTypeIgnoreCase(String workType, Pageable pageable);

    // ⭐ Cleaner search query for former teachers job board
    @Query("""
        SELECT j FROM Job j
        WHERE 
            (:keyword IS NULL OR :keyword = '' 
                OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
                OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:location IS NULL OR :location = '' OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
            AND (:category IS NULL OR :category = '' OR LOWER(j.category) = LOWER(:category))
            AND (:workType IS NULL OR :workType = '' OR LOWER(j.workType) = LOWER(:workType))
        ORDER BY j.createdAt DESC
    """)
    Page<Job> searchJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("category") String category,
            @Param("workType") String workType,
            Pageable pageable
    );
}