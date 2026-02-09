package com.capstone.adpl.repository;

import com.capstone.adpl.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    
    List<Result> findByStudentId(Long studentId);
    
    List<Result> findByQuizId(Long quizId);
    
    List<Result> findByQuizIdAndStudentId(Long quizId, Long studentId);
    
    Optional<Result> findTopByQuizIdAndStudentIdOrderByScoreDesc(Long quizId, Long studentId);
    
    @Query("SELECT COUNT(r) FROM Result r WHERE r.quiz.id = :quizId AND r.student.id = :studentId")
    int countAttemptsByQuizAndStudent(@Param("quizId") Long quizId, @Param("studentId") Long studentId);
    
    @Query("SELECT AVG(r.score) FROM Result r WHERE r.quiz.id = :quizId")
    Double getAverageScoreByQuizId(@Param("quizId") Long quizId);
    
    @Query("SELECT AVG(r.score * 100.0 / r.totalMarks) FROM Result r WHERE r.student.id = :studentId")
    Double getAveragePercentageByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT r FROM Result r WHERE r.quiz.course.id = :courseId AND r.student.id = :studentId ORDER BY r.completedAt DESC")
    List<Result> findByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(r) FROM Result r WHERE r.quiz.course.id = :courseId AND r.student.id = :studentId")
    long countByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(r) FROM Result r WHERE r.quiz.id = :quizId AND r.passed = true")
    long countPassedByQuizId(@Param("quizId") Long quizId);
}
