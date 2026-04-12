package com.formerteachers.repository;

import com.formerteachers.model.Application;
import com.formerteachers.model.Job;
import com.formerteachers.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByTeacher(Teacher teacher);
    List<Application> findByJob(Job job);
    Optional<Application> findByTeacherAndJob(Teacher teacher, Job job);
}
