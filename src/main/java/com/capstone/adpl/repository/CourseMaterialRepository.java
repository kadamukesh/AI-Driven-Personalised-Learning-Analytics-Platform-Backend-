package com.capstone.adpl.repository;

import com.capstone.adpl.model.CourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {
    
    List<CourseMaterial> findByCourseIdOrderByOrderIndexAsc(Long courseId);
    
    @Query("SELECT COUNT(m) FROM CourseMaterial m WHERE m.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
    
    void deleteByCourseId(Long courseId);
}
