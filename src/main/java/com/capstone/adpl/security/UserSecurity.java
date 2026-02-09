package com.capstone.adpl.security;

import com.capstone.adpl.repository.*;
import com.capstone.adpl.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final CourseMaterialRepository materialRepository;
    private final QuestionRepository questionRepository;

    public UserSecurity(CourseRepository courseRepository, UserRepository userRepository, 
                        QuizRepository quizRepository, 
                        CourseMaterialRepository materialRepository,
                        QuestionRepository questionRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.materialRepository = materialRepository;
        this.questionRepository = questionRepository;
    }

    public boolean isOwner(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }

    public boolean isCourseOwner(Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        return courseRepository.findById(courseId)
                .map(course -> course.getTeacher().getEmail().equals(email))
                .orElse(false);
    }

    public boolean isMaterialOwner(Long materialId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        return materialRepository.findById(materialId)
                .map(material -> material.getCourse().getTeacher().getEmail().equals(email))
                .orElse(false);
    }

    public boolean isQuizOwner(Long quizId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        return quizRepository.findById(quizId)
                .map(quiz -> quiz.getCourse().getTeacher().getEmail().equals(email))
                .orElse(false);
    }

    public boolean isQuestionOwner(Long questionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        return questionRepository.findById(questionId)
                .map(question -> question.getQuiz().getCourse().getTeacher().getEmail().equals(email))
                .orElse(false);
    }
}
