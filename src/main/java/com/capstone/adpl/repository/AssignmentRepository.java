package com.capstone.adpl.repository;

import com.capstone.adpl.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    List<Assignment> findByCourseId(Long courseId);
    
    List<Assignment> findByCourseIdOrderByDueDateAsc(Long courseId);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId AND a.dueDate > :now")
    List<Assignment> findUpcomingByCourseId(@Param("courseId") Long courseId, @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId AND a.dueDate <= :now")
    List<Assignment> findPastDueByCourseId(@Param("courseId") Long courseId, @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.teacher.id = :teacherId ORDER BY a.dueDate DESC")
    List<Assignment> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT a FROM Assignment a WHERE a.course.id IN (SELECT e.course.id FROM Enrollment e WHERE e.student.id = :studentId) ORDER BY a.dueDate ASC")
    List<Assignment> findByStudentEnrollment(@Param("studentId") Long studentId);
}
