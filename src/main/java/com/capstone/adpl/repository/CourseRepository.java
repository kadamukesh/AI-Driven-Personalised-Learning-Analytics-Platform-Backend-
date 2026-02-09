package com.capstone.adpl.repository;

import com.capstone.adpl.model.Course;
import com.capstone.adpl.model.DifficultyLevel;
import com.capstone.adpl.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByTeacher(User teacher);
    
    List<Course> findByTeacherId(Long teacherId);
    
    List<Course> findByPublishedTrue();
    
    List<Course> findByCategory(String category);
    
    List<Course> findByDifficulty(DifficultyLevel difficulty);
    
    @Query("SELECT c FROM Course c WHERE c.published = true AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Course> searchCourses(@Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT c.category FROM Course c WHERE c.category IS NOT NULL")
    List<String> findAllCategories();
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.teacher.id = :teacherId")
    long countByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.published = true")
    long countPublished();
}
