package com.capstone.adpl.controller;

import com.capstone.adpl.analytics.AnalyticsService;
import com.capstone.adpl.dto.response.AnalyticsResponse;
import com.capstone.adpl.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AuthService authService;

    public AnalyticsController(AnalyticsService analyticsService, AuthService authService) {
        this.analyticsService = analyticsService;
        this.authService = authService;
    }

    @GetMapping("/platform")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsResponse> getPlatformAnalytics() {
        return ResponseEntity.ok(analyticsService.getPlatformAnalytics());
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AnalyticsResponse> getCourseAnalytics(@PathVariable Long courseId) {
        return ResponseEntity.ok(analyticsService.getCourseAnalytics(courseId));
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or @userSecurity.isOwner(#studentId)")
    public ResponseEntity<AnalyticsResponse> getStudentCourseAnalytics(
            @PathVariable Long studentId, @PathVariable Long courseId) {
        return ResponseEntity.ok(analyticsService.getStudentAnalytics(studentId, courseId));
    }

    @GetMapping("/my/course/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AnalyticsResponse> getMyAnalytics(@PathVariable Long courseId) {
        Long studentId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(analyticsService.getStudentAnalytics(studentId, courseId));
    }

    @GetMapping("/recommendations")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<String>> getRecommendations() {
        Long studentId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(analyticsService.getRecommendations(studentId));
    }

    @PostMapping("/update/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AnalyticsResponse> updateMyAnalytics(@PathVariable Long courseId) {
        Long studentId = authService.getCurrentUser().getId();
        analyticsService.updateStudentAnalytics(studentId, courseId);
        return ResponseEntity.ok(analyticsService.getStudentAnalytics(studentId, courseId));
    }

    @GetMapping("/teacher/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<AnalyticsResponse>> getTeacherStudentAnalytics() {
        Long teacherId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(analyticsService.getTeacherStudentsAnalytics(teacherId));
    }

    @GetMapping("/my/courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AnalyticsResponse>> getMyAllCoursesAnalytics() {
        Long studentId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(analyticsService.getStudentAllCoursesAnalytics(studentId));
    }
}
