package com.capstone.adpl.repository;

import com.capstone.adpl.model.RiskLevel;
import com.capstone.adpl.model.StudentAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnalyticsRepository extends JpaRepository<StudentAnalytics, Long> {
    
    List<StudentAnalytics> findByStudentId(Long studentId);
    
    List<StudentAnalytics> findByCourseId(Long courseId);
    
    Optional<StudentAnalytics> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<StudentAnalytics> findByRiskLevel(RiskLevel riskLevel);
    
    @Query("SELECT sa FROM StudentAnalytics sa WHERE sa.course.id = :courseId AND sa.riskLevel IN ('HIGH', 'CRITICAL')")
    List<StudentAnalytics> findAtRiskStudentsByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT AVG(sa.overallScore) FROM StudentAnalytics sa WHERE sa.course.id = :courseId")
    Double getAverageScoreByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT AVG(sa.engagementRate) FROM StudentAnalytics sa WHERE sa.course.id = :courseId")
    Double getAverageEngagementByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT sa FROM StudentAnalytics sa WHERE sa.course.teacher.id = :teacherId ORDER BY sa.overallScore ASC")
    List<StudentAnalytics> findLowestPerformingByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT COUNT(sa) FROM StudentAnalytics sa WHERE sa.course.id = :courseId AND sa.completionRate = 100")
    long countCompletedByCourseId(@Param("courseId") Long courseId);
}
