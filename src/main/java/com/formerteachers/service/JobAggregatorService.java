package com.formerteachers.service;

import com.formerteachers.model.Job;
import com.formerteachers.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Service
public class JobAggregatorService {

    private final JobRepository jobRepository;
    private final RestTemplate restTemplate;

    public JobAggregatorService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
        this.restTemplate = new RestTemplate();
    }

    // ===============================
    // IMPORT JOBS WITH CATEGORY
    // ===============================
    public int importJobsFromApi(String apiUrl, String category) {
        int importedCount = 0;

        try {
            // Example: call API (adjust depending on actual API structure)
            ResponseEntity<JobApiResponse> response = restTemplate.getForEntity(apiUrl, JobApiResponse.class);
            JobApiResponse apiResponse = response.getBody();

            if (apiResponse != null && apiResponse.jobs != null) {
                for (JobData jobData : apiResponse.jobs) {
                    // Optionally filter by category
                    if (category == null || category.isEmpty() || category.equalsIgnoreCase(jobData.category)) {
                        Job job = new Job();
                        job.setTitle(jobData.title);
                        job.setCompany(jobData.company);
                        job.setLocation(jobData.location);
                        job.setDescription(jobData.description);
                        job.setCategory(jobData.category);
                        job.setWorkType(jobData.jobType);
                        job.setSalaryRange(jobData.salary);

                        jobRepository.save(job);
                        importedCount++;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error importing jobs: " + e.getMessage());
        }

        System.out.println("Scheduled import completed. Jobs added: " + importedCount);
        return importedCount;
    }

    // ===============================
    // SIMPLE IMPORT METHOD (DEFAULT CATEGORY)
    // ===============================
    public int importJobsFromApi(String apiUrl) {
        String defaultCategory = "education";  // default for HomeController & startup
        return importJobsFromApi(apiUrl, defaultCategory);
    }

    // ===============================
    // INNER CLASSES TO MAP API RESPONSE
    // ===============================
    public static class JobApiResponse {
        public List<JobData> jobs;
    }

    public static class JobData {
        public String title;
        public String company;
        public String location;
        public String description;
        public String category;
        public String jobType;
        public String salary;
    }
}