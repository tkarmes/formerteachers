package com.formerteachers.service;

import com.formerteachers.model.EmployerProfile;
import com.formerteachers.model.Job;
import com.formerteachers.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Page<Job> getApprovedJobs(String keyword, String location, String category, String workType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        String keywordLower = (keyword != null && !keyword.isEmpty()) ? "%" + keyword.toLowerCase() + "%" : null;
        String locationLower = (location != null && !location.isEmpty()) ? "%" + location.toLowerCase() + "%" : null;
        
        return jobRepository.searchApprovedJobs(keywordLower, locationLower, category, workType, pageable);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    public List<Job> getFeaturedJobs() {
        return jobRepository.findTop3ByApprovedTrueOrderByCreatedAtDesc();
    }

    public List<Job> getJobsByEmployer(EmployerProfile employer) {
        return jobRepository.findByEmployer(employer);
    }

    public List<Job> getPendingJobs() {
        return jobRepository.findByApprovedFalseOrderByCreatedAtDesc();
    }

    public void approveJob(Long id) {
        Job job = getJobById(id);
        job.setApproved(true);
        jobRepository.save(job);
    }

    public void deleteJobById(Long id) {
        jobRepository.deleteById(id);
    }

    public Job getJobByIdAndEmployer(Long id, EmployerProfile employer) {
        Job job = getJobById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("You do not have permission to access this job.");
        }
        return job;
    }
}
