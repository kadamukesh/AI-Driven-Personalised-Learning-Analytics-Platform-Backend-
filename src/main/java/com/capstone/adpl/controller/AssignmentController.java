package com.capstone.adpl.controller;

import com.capstone.adpl.dto.request.AssignmentRequest;
import com.capstone.adpl.dto.request.GradeSubmissionRequest;
import com.capstone.adpl.dto.request.SubmissionRequest;
import com.capstone.adpl.dto.response.ApiResponse;
import com.capstone.adpl.dto.response.AssignmentResponse;
import com.capstone.adpl.dto.response.SubmissionResponse;
import com.capstone.adpl.model.Assignment;
import com.capstone.adpl.model.Submission;
import com.capstone.adpl.service.AssignmentService;
import com.capstone.adpl.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AuthService authService;

    public AssignmentController(AssignmentService assignmentService, AuthService authService) {
        this.assignmentService = assignmentService;
        this.authService = authService;
    }

    @GetMapping("/assignments/teacher")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<AssignmentResponse>> getTeacherAssignments() {
        Long teacherId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(assignmentService.getAssignmentsByTeacher(teacherId));
    }

    @GetMapping("/assignments/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AssignmentResponse>> getMyAssignments() {
        Long studentId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(assignmentService.getAssignmentsByStudent(studentId));
    }

    @GetMapping("/courses/{courseId}/assignments")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByCourse(courseId));
    }

    @GetMapping("/assignments/{id}")
    public ResponseEntity<AssignmentResponse> getAssignment(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getAssignmentResponseById(id));
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AssignmentResponse> createAssignment(@Valid @RequestBody AssignmentRequest request) {
        Long teacherId = authService.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.createAssignment(request, teacherId));
    }

    @PutMapping("/assignments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<AssignmentResponse> updateAssignment(@PathVariable Long id, @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.updateAssignment(id, request));
    }

    @DeleteMapping("/assignments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(ApiResponse.success("Assignment deleted"));
    }

    // Submission endpoints
    @PostMapping("/assignments/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponse> submitAssignment(@PathVariable Long id, @RequestBody SubmissionRequest request) {
        Long studentId = authService.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubmissionResponse.fromEntity(assignmentService.submitAssignment(id, studentId, request)));
    }

    @GetMapping("/assignments/{id}/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<SubmissionResponse>> getSubmissions(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getSubmissionsByAssignment(id));
    }

    @GetMapping("/assignments/{id}/my-submission")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponse> getMySubmission(@PathVariable Long id) {
        Long studentId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(assignmentService.getSubmission(id, studentId));
    }

    @PutMapping("/submissions/{id}/grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<SubmissionResponse> gradeSubmission(@PathVariable Long id, @Valid @RequestBody GradeSubmissionRequest request) {
        return ResponseEntity.ok(assignmentService.gradeSubmission(id, request));
    }
}
