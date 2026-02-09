package com.capstone.adpl.repository;

import com.capstone.adpl.model.Submission;
import com.capstone.adpl.model.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    List<Submission> findByAssignmentId(Long assignmentId);
    
    List<Submission> findByStudentId(Long studentId);
    
    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    
    boolean existsByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    
    List<Submission> findByAssignmentIdAndStatus(Long assignmentId, SubmissionStatus status);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId")
    long countByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.assignment.id = :assignmentId AND s.score IS NOT NULL")
    Double getAverageScoreByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.student.id = :studentId AND s.score IS NOT NULL")
    Double getAverageScoreByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT s FROM Submission s WHERE s.assignment.course.id = :courseId AND s.student.id = :studentId")
    List<Submission> findByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.course.id = :courseId AND s.student.id = :studentId")
    long countByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
