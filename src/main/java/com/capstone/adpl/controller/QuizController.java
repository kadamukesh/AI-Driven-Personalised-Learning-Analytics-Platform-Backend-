package com.capstone.adpl.controller;

import com.capstone.adpl.dto.request.QuizRequest;
import com.capstone.adpl.dto.request.QuizSubmitRequest;
import com.capstone.adpl.dto.response.ApiResponse;
import com.capstone.adpl.dto.response.QuestionResponse;
import com.capstone.adpl.dto.response.QuizResponse;
import com.capstone.adpl.dto.response.ResultResponse;
import com.capstone.adpl.model.Question;
import com.capstone.adpl.model.Result;
import com.capstone.adpl.service.AuthService;
import com.capstone.adpl.service.QuizService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(QuizController.class);

    public QuizController(QuizService quizService, AuthService authService) {
        this.quizService = quizService;
        this.authService = authService;
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<QuizResponse>> getTeacherQuizzes() {
        Long teacherId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(quizService.getQuizzesByTeacher(teacherId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuizResponse>> getQuizzesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(quizService.getQuizzesByCourse(courseId));
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<QuizResponse>> getAvailableQuizzes(@RequestParam(required = false) Long courseId) {
        if (courseId != null) {
            return ResponseEntity.ok(quizService.getAvailableQuizzes(courseId));
        } else {
            Long studentId = authService.getCurrentUser().getId();
            return ResponseEntity.ok(quizService.getAvailableQuizzesForStudent(studentId));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizResponseById(id));
    }

    @GetMapping("/{id}/start")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> startQuiz(@PathVariable Long id) {
        log.info("Starting quiz attempt for quiz ID: {}", id);
        QuizResponse quiz = quizService.getQuizResponseById(id);
        List<QuestionResponse> questions = quizService.getQuizQuestions(id).stream()
                .map(QuestionResponse::fromEntity)
                .peek(q -> q.setCorrectAnswer(null)) // Extra safety
                .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("quiz", quiz);
        response.put("questions", questions);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @userSecurity.isCourseOwner(#request.courseId))")
    public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest request) {
        log.info("Creating new quiz: {}", request.getTitle());
        Long teacherId = authService.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizService.createQuiz(request, teacherId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @userSecurity.isQuizOwner(#id))")
    public ResponseEntity<QuizResponse> updateQuiz(@PathVariable Long id, @Valid @RequestBody QuizRequest request) {
        log.info("Updating quiz with ID: {}", id);
        Long teacherId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(quizService.updateQuiz(id, request, teacherId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @userSecurity.isQuizOwner(#id))")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long id) {
        log.info("Deleting quiz with ID: {}", id);
        quizService.deleteQuiz(id);
        return ResponseEntity.ok(ApiResponse.success("Quiz deleted successfully"));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResultResponse> submitQuiz(@PathVariable Long id, @RequestBody QuizSubmitRequest request) {
        log.info("Submitting quiz result for quiz ID: {}", id);
        Long studentId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(ResultResponse.fromEntity(quizService.submitQuiz(id, studentId, request)));
    }

    @GetMapping("/{id}/results")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @userSecurity.isQuizOwner(#id))")
    public ResponseEntity<List<ResultResponse>> getQuizResults(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizResults(id).stream().map(ResultResponse::fromEntity).toList());
    }

    @GetMapping("/{id}/my-best-result")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResultResponse> getMyBestResult(@PathVariable Long id) {
        Long studentId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(ResultResponse.fromEntity(quizService.getBestResult(id, studentId)));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ResultResponse>> getMyResultsHistory() {
        Long studentId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(quizService.getStudentResults(studentId).stream().map(ResultResponse::fromEntity).toList());
    }
}
