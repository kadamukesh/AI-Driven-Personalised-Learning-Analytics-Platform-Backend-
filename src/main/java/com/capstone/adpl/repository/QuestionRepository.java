package com.capstone.adpl.repository;

import com.capstone.adpl.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByQuizIdOrderByOrderIndexAsc(Long quizId);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.quiz.id = :quizId")
    long countByQuizId(@Param("quizId") Long quizId);
    
    @Query("SELECT SUM(q.marks) FROM Question q WHERE q.quiz.id = :quizId")
    Integer getTotalMarksByQuizId(@Param("quizId") Long quizId);
    
    void deleteByQuizId(Long quizId);
    
    @Query("SELECT DISTINCT q.topicTag FROM Question q WHERE q.quiz.course.id = :courseId AND q.topicTag IS NOT NULL")
    List<String> findDistinctTopicsByCourseId(@Param("courseId") Long courseId);
}
