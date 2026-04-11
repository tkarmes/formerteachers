package com.formerteachers.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String fullName;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    private Integer yearsInClassroom;
    
    private String subjectSpecialty;
    
    private String desiredRole;

    private String profileImageUrl;
    
    private String resumeUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teacher_saved_jobs",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "job_id")
    )
    private Set<Job> savedJobs = new HashSet<>();

    public Teacher() {}

    public Teacher(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getYearsInClassroom() {
        return yearsInClassroom;
    }

    public void setYearsInClassroom(Integer yearsInClassroom) {
        this.yearsInClassroom = yearsInClassroom;
    }

    public String getSubjectSpecialty() {
        return subjectSpecialty;
    }

    public void setSubjectSpecialty(String subjectSpecialty) {
        this.subjectSpecialty = subjectSpecialty;
    }

    public String getDesiredRole() {
        return desiredRole;
    }

    public void setDesiredRole(String desiredRole) {
        this.desiredRole = desiredRole;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public Set<Job> getSavedJobs() {
        return savedJobs;
    }

    public void setSavedJobs(Set<Job> savedJobs) {
        this.savedJobs = savedJobs;
    }

    public boolean isProfileComplete() {
        return fullName != null && !fullName.isEmpty() &&
               bio != null && !bio.isEmpty() &&
               yearsInClassroom != null &&
               subjectSpecialty != null && !subjectSpecialty.isEmpty() &&
               desiredRole != null && !desiredRole.isEmpty();
    }
}
