package com.formerteachers.dto;

import java.util.List;

public class JobPageDTO {

    private List<JobDTO> content;   // List of JobDTOs
    private int pageNumber;         // Current page index (0-based)
    private int pageSize;           // Number of items per page
    private long totalElements;     // Total jobs in DB
    private int totalPages;         // Total number of pages
    private boolean last;           // Is this the last page?

    public JobPageDTO() {}

    public JobPageDTO(List<JobDTO> content, int pageNumber, int pageSize,
                      long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    // Getters and setters
    public List<JobDTO> getContent() { return content; }
    public void setContent(List<JobDTO> content) { this.content = content; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
}