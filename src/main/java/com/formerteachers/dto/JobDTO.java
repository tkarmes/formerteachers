package com.formerteachers.dto;

import java.time.LocalDateTime;

public class JobDTO {

    private Long id;
    private String title;
    private String company;
    private String location;
    private String salaryRange;
    private String description;
    private String category;
    private String workType;
    private String applyInfo;
    private LocalDateTime createdAt;

    // Constructor
    public JobDTO(Long id, String title, String company, String location,
                  String salaryRange, String description, String category,
                  String workType, String applyInfo, LocalDateTime createdAt) {

        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salaryRange = salaryRange;
        this.description = description;
        this.category = category;
        this.workType = workType;
        this.applyInfo = applyInfo;
        this.createdAt = createdAt;
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
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}