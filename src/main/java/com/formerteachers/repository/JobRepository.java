package com.formerteachers.repository;

import com.formerteachers.model.EmployerProfile;
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
    List<Job> findByCategory(String category);
    List<Job> findByEmployer(EmployerProfile employer);
    List<Job> findByApprovedTrue();
    List<Job> findByApprovedFalse();
    List<Job> findByApprovedFalseOrderByCreatedAtDesc();
    List<Job> findTop3ByApprovedTrueOrderByCreatedAtDesc();

    @Query("SELECT j FROM Job j WHERE " +
           "j.approved = true AND " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(:keyword) OR LOWER(j.description) LIKE LOWER(:keyword) OR LOWER(j.company) LIKE LOWER(:keyword)) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(:location)) AND " +
           "(:category IS NULL OR j.category = :category) AND " +
           "(:workType IS NULL OR j.workType = :workType)")
    Page<Job> searchApprovedJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("category") String category,
            @Param("workType") String workType,
            Pageable pageable);

    @Query("SELECT j FROM Job j WHERE " +
           "LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Job> searchByKeyword(@Param("query") String query);
}
