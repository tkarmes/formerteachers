package com.formerteachers.dto;

public record JobDTO(
        Long id,
        String title,
        String company,
        String location,
        String salaryRange,
        String description
) {}