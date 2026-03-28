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

    @Query("SELECT j FROM Job j WHERE j.approved = true AND " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE :keyword OR LOWER(j.description) LIKE :keyword) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE :location) AND " +
           "(:category IS NULL OR j.category = :category) AND " +
           "(:workType IS NULL OR j.workType = :workType)")
    Page<Job> searchApprovedJobs(@Param("keyword") String keyword,
                                 @Param("location") String location,
                                 @Param("category") String category,
                                 @Param("workType") String workType,
                                 Pageable pageable);

    List<Job> findByEmployer(EmployerProfile employer);

    List<Job> findByApprovedFalseOrderByCreatedAtDesc();

    List<Job> findTop3ByApprovedTrueOrderByCreatedAtDesc();
}
