package com.formerteachers.service;

import com.formerteachers.model.Application;
import com.formerteachers.model.Job;
import com.formerteachers.model.Teacher;
import com.formerteachers.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    public Application apply(Teacher teacher, Job job, String coverLetter) {
        // Check if already applied
        Optional<Application> existing = applicationRepository.findByTeacherAndJob(teacher, job);
        if (existing.isPresent()) {
            return existing.get();
        }

        Application application = new Application();
        application.setTeacher(teacher);
        application.setJob(job);
        application.setCoverLetter(coverLetter);
        application.setResumeUrl(teacher.getResumeUrl()); // Capture the resume version at application time
        application.setStatus("PENDING");

        return applicationRepository.save(application);
    }

    public List<Application> getTeacherApplications(Teacher teacher) {
        return applicationRepository.findByTeacher(teacher);
    }

    public List<Application> getJobApplications(Job job) {
        return applicationRepository.findByJob(job);
    }

    public boolean hasApplied(Teacher teacher, Job job) {
        return applicationRepository.findByTeacherAndJob(teacher, job).isPresent();
    }

    public Application updateStatus(Long applicationId, String status) {
        Optional<Application> optionalApplication = applicationRepository.findById(applicationId);
        if (optionalApplication.isPresent()) {
            Application application = optionalApplication.get();
            application.setStatus(status);
            return applicationRepository.save(application);
        }
        return null;
    }

    public Optional<Application> getById(Long id) {
        return applicationRepository.findById(id);
    }
}
