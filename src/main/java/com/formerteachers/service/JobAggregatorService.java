package com.formerteachers.service;

import com.formerteachers.model.Job;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.Iterator;

@Service
public class JobAggregatorService {

    private final JobService jobService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public JobAggregatorService(JobService jobService) {
        this.jobService = jobService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // Runs automatically every 6 hours
    @Scheduled(fixedRate = 21600000)
    public void scheduledImport() {

        String apiUrl = "https://remotive.com/api/remote-jobs?search=education";

        int importedCount = importJobsFromApi(apiUrl);

        System.out.println("Scheduled import completed. Jobs added: " + importedCount);
    }

    public int importJobsFromApi(String apiUrl) {
        int importedCount = 0;

        try {
            // Call the API
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    String.class
            );

            if (response.getBody() == null) return 0;

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode jobsArray = root.path("jobs");

            if (!jobsArray.isArray()) return 0;

            // Loop through jobs
            for (JsonNode j : jobsArray) {
                Job job = new Job();
                job.setTitle(j.path("title").asText());
                job.setCompany(j.path("company_name").asText("Unknown"));
                job.setLocation(j.path("candidate_required_location").asText("Remote"));
                job.setWorkType(j.path("job_type").asText(""));
                job.setDescription(j.path("description").asText(""));
                job.setSalaryRange("");
                job.setCategory(autoTag(job.getTitle(), job.getDescription()));

                // Save every job without checking for duplicates
                jobService.save(job);
                importedCount++;
            }

        } catch (Exception e) {
            System.err.println("Job import failed: " + e.getMessage());
        }

        return importedCount;
    }
    private String autoTag(String title, String description) {

        String combined = (title + " " + description).toLowerCase();

        if (combined.contains("instructional designer"))
            return "Instructional Design";

        if (combined.contains("curriculum"))
            return "Curriculum Development";

        if (combined.contains("edtech"))
            return "Educational Technology";

        if (combined.contains("teacher"))
            return "Teaching";

        if (combined.contains("training"))
            return "Corporate Training";

        return "Other";
    }
}