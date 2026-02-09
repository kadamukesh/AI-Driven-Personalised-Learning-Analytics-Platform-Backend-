package com.capstone.adpl.repository;

import com.capstone.adpl.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    List<Quiz> findByCourseId(Long courseId);
    
    List<Quiz> findByCourseIdAndPublishedTrue(Long courseId);
    
    @Query("SELECT q FROM Quiz q WHERE q.course.id = :courseId AND q.published = true AND (q.startTime IS NULL OR q.startTime <= :now) AND (q.endTime IS NULL OR q.endTime >= :now)")
    List<Quiz> findAvailableQuizzes(@Param("courseId") Long courseId, @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT q FROM Quiz q JOIN Enrollment e ON q.course.id = e.course.id " +
           "WHERE e.student.id = :studentId AND q.published = true " +
           "AND (q.startTime IS NULL OR q.startTime <= :now) " +
           "AND (q.endTime IS NULL OR q.endTime >= :now)")
    List<Quiz> findAllAvailableQuizzesForStudent(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);

    @Query("SELECT q FROM Quiz q WHERE q.course.teacher.id = :teacherId ORDER BY q.createdAt DESC")
    List<Quiz> findByTeacherId(@Param("teacherId") Long teacherId);
}
