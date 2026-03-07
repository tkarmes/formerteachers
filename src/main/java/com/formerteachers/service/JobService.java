package com.formerteachers.service;

import com.formerteachers.model.Job;
import com.formerteachers.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Page<Job> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    public Page<Job> getJobsByCompany(String company, Pageable pageable) {
        return jobRepository.findByCompanyIgnoreCase(company, pageable);
    }

    public Page<Job> getJobsByCategory(String category, Pageable pageable) {
        return jobRepository.findByCategoryIgnoreCase(category, pageable);
    }

    public Page<Job> getJobsByLocation(String location, Pageable pageable) {
        return jobRepository.findByLocationIgnoreCase(location, pageable);
    }

    public Page<Job> getJobsByWorkType(String workType, Pageable pageable) {
        return jobRepository.findByWorkTypeIgnoreCase(workType, pageable);
    }

    // ⭐ Combined search method
    public Page<Job> searchJobs(String keyword, String location, String category, String workType, Pageable pageable) {
        // Make nulls safe for repository query
        String safeKeyword = (keyword == null) ? "" : keyword;
        String safeLocation = (location == null) ? "" : location;
        String safeCategory = (category == null) ? "" : category;
        String safeWorkType = (workType == null) ? "" : workType;

        return jobRepository.searchJobs(safeKeyword, safeLocation, safeCategory, safeWorkType, pageable);
    }

    public Job save(Job job) {
        return jobRepository.save(job);
    }

    public void delete(Job job) {
        jobRepository.delete(job);
    }
}