package com.formerteachers.service;

import com.formerteachers.model.Job;
import com.formerteachers.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // GET all jobs with pageable & default sort
    public Page<Job> getAllJobs(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );
        return jobRepository.findAll(sortedPageable);
    }

    // GET all jobs without pagination
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // DELETE by ID
    public void deleteById(Long id) {
        jobRepository.deleteById(id);
    }

    // GET job by ID
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    // GET jobs by company
    public Page<Job> getJobsByCompany(String company, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );
        return jobRepository.findByCompanyIgnoreCase(company, sortedPageable);
    }

    // GET jobs by category
    public Page<Job> getJobsByCategory(String category, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );
        return jobRepository.findByCategoryIgnoreCase(category, sortedPageable);
    }

    // GET jobs by location
    public Page<Job> getJobsByLocation(String location, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );
        return jobRepository.findByLocationIgnoreCase(location, sortedPageable);
    }

    // GET jobs by work type
    public Page<Job> getJobsByWorkType(String workType, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );
        return jobRepository.findByWorkTypeIgnoreCase(workType, sortedPageable);
    }

    // SEARCH jobs with combined filters
    public Page<Job> searchJobs(String keyword, String location, String category, String workType, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );

        String safeKeyword = (keyword == null) ? "" : keyword;
        String safeLocation = (location == null) ? "" : location;
        String safeCategory = (category == null) ? "" : category;
        String safeWorkType = (workType == null) ? "" : workType;

        return jobRepository.searchJobs(safeKeyword, safeLocation, safeCategory, safeWorkType, sortedPageable);
    }

    // SAVE job
    public Job save(Job job) {
        return jobRepository.save(job);
    }

    // DELETE job
    public void delete(Job job) {
        jobRepository.delete(job);
    }

    // GET featured jobs (top 3 newest)
    public List<Job> getFeaturedJobs() {
        return jobRepository.findTop3ByOrderByCreatedAtDesc();
    }

    // CHECK if job exists by title/company/location
    public boolean jobExists(Job job) {
        return jobRepository.existsByTitleAndCompanyAndLocation(
                job.getTitle(),
                job.getCompany(),
                job.getLocation()
        );
    }
}