package com.formerteachers.dto;

public class JobDTO {

    private Long id;
    private String title;
    private String company;
    private String location;
    private String salaryRange;
    private String description;
    private String category;

    // Constructor
    public JobDTO(Long id, String title, String company, String location,
                  String salaryRange, String description, String category) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salaryRange = salaryRange;
        this.description = description;
        this.category = category;
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
}