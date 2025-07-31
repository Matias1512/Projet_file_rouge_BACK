package com.learncode.schoolDev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.learncode.schoolDev.model.Lesson;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourse_CourseId(Long courseId);
    
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.course.courseId = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT l FROM Lesson l WHERE l.course.courseId = :courseId ORDER BY l.orderInCourse")
    List<Lesson> findByCourse_CourseIdOrderByOrderInCourse(@Param("courseId") Long courseId);
}
