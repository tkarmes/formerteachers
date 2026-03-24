package com.formerteachers.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import com.formerteachers.util.DateFormatter;

@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Company is required")
    private String company;

    private String location;

    private String salaryRange;

    @NotBlank(message = "Description is required")

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean approved = false;

    // --- UPDATED: Added validation here ---
    @NotBlank(message = "Category is required")
    private String category;

    private String workType;

    private String applyInfo;

    private LocalDateTime createdAt;

    @Transient
    private String postedDate;

    public Job() {}

    public Job(String title, String company, String location, String salaryRange,
               String description, String category, String workType) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.salaryRange = salaryRange;
        this.description = description;
        this.category = category;
        this.workType = workType;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PostLoad
    protected void calculatePostedDate() {
        this.postedDate = DateFormatter.formatPostedDate(this.createdAt);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getWorkType() { return workType; }
    public void setWorkType(String workType) { this.workType = workType; }

    public String getApplyInfo() { return applyInfo; }
    public void setApplyInfo(String applyInfo) { this.applyInfo = applyInfo; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getPostedDate() { return postedDate; }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

}