package com.capstone.adpl.controller;

import com.capstone.adpl.dto.response.ApiResponse;
import com.capstone.adpl.dto.response.CourseResponse;
import com.capstone.adpl.model.Enrollment;
import com.capstone.adpl.service.AuthService;
import com.capstone.adpl.service.CourseService;
import com.capstone.adpl.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final AuthService authService;
    private final CourseService courseService;

    public EnrollmentController(EnrollmentService enrollmentService, AuthService authService, CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.authService = authService;
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> enroll(@RequestBody Map<String, Long> request) {
        Long studentId = authService.getCurrentUser().getId();
        Long courseId = request.get("courseId");
        enrollmentService.enrollStudent(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Enrolled successfully"));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseResponse>> getMyEnrollments() {
        Long studentId = authService.getCurrentUser().getId();
        List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(studentId);
        List<CourseResponse> courses = enrollments.stream()
                .map(e -> courseService.getCourseById(e.getCourse().getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/course/{courseId}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<Enrollment>> getCourseStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getCourseEnrollments(courseId));
    }

    @GetMapping("/check/{courseId}")
    public ResponseEntity<Map<String, Boolean>> checkEnrollment(@PathVariable Long courseId) {
        Long studentId = authService.getCurrentUser().getId();
        boolean enrolled = enrollmentService.isEnrolled(studentId, courseId);
        return ResponseEntity.ok(Map.of("enrolled", enrolled));
    }

    @PutMapping("/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> updateProgress(@RequestBody Map<String, Object> request) {
        Long studentId = authService.getCurrentUser().getId();
        Long courseId = ((Number) request.get("courseId")).longValue();
        Double progress = ((Number) request.get("progress")).doubleValue();
        enrollmentService.updateProgress(studentId, courseId, progress);
        return ResponseEntity.ok(ApiResponse.success("Progress updated"));
    }

    @PostMapping("/drop/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> dropCourse(@PathVariable Long courseId) {
        Long studentId = authService.getCurrentUser().getId();
        enrollmentService.dropCourse(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Course dropped successfully"));
    }
}
