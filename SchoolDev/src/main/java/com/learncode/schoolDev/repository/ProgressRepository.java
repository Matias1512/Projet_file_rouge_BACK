package com.learncode.schoolDev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learncode.schoolDev.model.Progress;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUser_UserId(Long userId);
    List<Progress> findByCourse_CourseId(Long courseId);
    Optional<Progress> findByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);
}
