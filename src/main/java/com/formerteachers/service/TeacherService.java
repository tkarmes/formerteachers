package com.formerteachers.service;

import com.formerteachers.model.Job;
import com.formerteachers.model.Teacher;
import com.formerteachers.model.User;
import com.formerteachers.repository.JobRepository;
import com.formerteachers.repository.TeacherRepository;
import com.formerteachers.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public TeacherService(TeacherRepository teacherRepository, JobRepository jobRepository, UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveJob(String username, Long jobId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Teacher teacher = getOrCreateTeacher(user);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        teacher.getSavedJobs().add(job);
        teacherRepository.save(teacher);
    }

    @Transactional
    public void unsaveJob(String username, Long jobId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        teacherRepository.findByUser(user).ifPresent(teacher -> {
            teacher.getSavedJobs().removeIf(job -> job.getId().equals(jobId));
            teacherRepository.save(teacher);
        });
    }

    public Set<Job> getSavedJobs(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return teacherRepository.findByUser(user)
                .map(Teacher::getSavedJobs)
                .orElse(Collections.emptySet());
    }

    public Teacher getTeacherProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return getOrCreateTeacher(user);
    }

    @Transactional
    public void updateProfile(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    @Transactional
    public void updateProfile(String username, Teacher profileData) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Teacher teacher = getOrCreateTeacher(user);
        
        teacher.setFullName(profileData.getFullName());
        teacher.setBio(profileData.getBio());
        teacher.setYearsInClassroom(profileData.getYearsInClassroom());
        teacher.setSubjectSpecialty(profileData.getSubjectSpecialty());
        teacher.setDesiredRole(profileData.getDesiredRole());
        teacher.setProfileImageUrl(profileData.getProfileImageUrl());
        teacher.setPublicProfile(profileData.isPublicProfile());
        
        teacherRepository.save(teacher);
    }

    public List<Teacher> getPublicTeachers() {
        return teacherRepository.findByPublicProfileTrue();
    }

    private Teacher getOrCreateTeacher(User user) {
        return teacherRepository.findByUser(user)
                .orElseGet(() -> {
                    Teacher newTeacher = new Teacher();
                    newTeacher.setUser(user);
                    return teacherRepository.save(newTeacher);
                });
    }

    public Teacher findByUser(User user) {
        return getOrCreateTeacher(user);
    }
}
