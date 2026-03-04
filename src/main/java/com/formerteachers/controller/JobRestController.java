package com.formerteachers.controller;

import com.formerteachers.model.Job;
import com.formerteachers.repository.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobRestController {

    private final JobRepository jobRepository;

    public JobRestController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // GET all jobs
    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // GET single job
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return jobRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE job (POST)
    @PostMapping
    public ResponseEntity<Job> createJob(@Valid @RequestBody Job job) {
        Job savedJob = jobRepository.save(job);

        // Build the URI for the new resource
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedJob.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedJob);
    }

    // UPDATE job (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id,
                                         @Valid @RequestBody Job updatedJob) {
        return jobRepository.findById(id)
                .map(job -> {
                    job.setTitle(updatedJob.getTitle());
                    job.setCompany(updatedJob.getCompany());
                    job.setLocation(updatedJob.getLocation());
                    job.setSalaryRange(updatedJob.getSalaryRange());
                    job.setDescription(updatedJob.getDescription());
                    jobRepository.save(job);
                    return ResponseEntity.ok(job);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        return jobRepository.findById(id)
                .map(job -> {
                    jobRepository.delete(job);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}