package com.capstone.adpl.service;

import com.capstone.adpl.dto.request.AssignmentRequest;
import com.capstone.adpl.dto.request.GradeSubmissionRequest;
import com.capstone.adpl.dto.request.SubmissionRequest;
import com.capstone.adpl.dto.response.ApiResponse;
import com.capstone.adpl.dto.response.AssignmentResponse;
import com.capstone.adpl.dto.response.SubmissionResponse;
import com.capstone.adpl.exception.BadRequestException;
import com.capstone.adpl.exception.ResourceNotFoundException;
import com.capstone.adpl.model.*;
import com.capstone.adpl.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository,
                             CourseRepository courseRepository, UserRepository userRepository,
                             EnrollmentRepository enrollmentRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<AssignmentResponse> getAssignmentsByTeacher(Long teacherId) {
        return assignmentRepository.findByTeacherId(teacherId).stream()
                .map(AssignmentResponse::fromEntity)
                .toList();
    }

    public List<AssignmentResponse> getAssignmentsByStudent(Long studentId) {
        return assignmentRepository.findByStudentEnrollment(studentId).stream()
                .map(assignment -> {
                    AssignmentResponse response = AssignmentResponse.fromEntity(assignment);
                    submissionRepository.findByAssignmentIdAndStudentId(assignment.getId(), studentId)
                            .ifPresent(submission -> {
                                response.setScore(submission.getScore());
                                response.setStatus(submission.getStatus().name());
                            });
                    return response;
                })
                .toList();
    }

    public List<AssignmentResponse> getAssignmentsByCourse(Long courseId) {
        return assignmentRepository.findByCourseIdOrderByDueDateAsc(courseId).stream()
                .map(AssignmentResponse::fromEntity)
                .toList();
    }

    public AssignmentResponse getAssignmentResponseById(Long id) {
        return AssignmentResponse.fromEntity(getAssignmentById(id));
    }

    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));
    }

    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest request, Long teacherId) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        // Verify teacher owns the course
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new BadRequestException("You are not authorized to create assignments for this course");
        }

        Assignment assignment = new Assignment();
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setInstructions(request.getInstructions());
        assignment.setCourse(course);
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxScore(request.getMaxScore() != null ? request.getMaxScore() : 100);
        assignment.setTopicTags(request.getTopicTags());
        assignment.setMaterialUrl(request.getMaterialUrl());
        assignment.setMaterialType(request.getMaterialType());

        return AssignmentResponse.fromEntity(assignmentRepository.save(assignment));
    }

    @Transactional
    public AssignmentResponse updateAssignment(Long id, AssignmentRequest request) {
        Assignment assignment = getAssignmentById(id);

        if (request.getTitle() != null) {
            assignment.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            assignment.setDescription(request.getDescription());
        }
        if (request.getInstructions() != null) {
            assignment.setInstructions(request.getInstructions());
        }
        if (request.getDueDate() != null) {
            assignment.setDueDate(request.getDueDate());
        }
        if (request.getMaxScore() != null) {
            assignment.setMaxScore(request.getMaxScore());
        }
        if (request.getTopicTags() != null) {
            assignment.setTopicTags(request.getTopicTags());
        }
        if (request.getMaterialUrl() != null) {
            assignment.setMaterialUrl(request.getMaterialUrl());
        }
        if (request.getMaterialType() != null) {
            assignment.setMaterialType(request.getMaterialType());
        }

        return AssignmentResponse.fromEntity(assignmentRepository.save(assignment));
    }

    @Transactional
    public void deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Assignment", "id", id);
        }
        assignmentRepository.deleteById(id);
    }

    // Submission methods
    @Transactional
    public Submission submitAssignment(Long assignmentId, Long studentId, SubmissionRequest request) {
        Assignment assignment = getAssignmentById(assignmentId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        // Check if student is enrolled
        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, assignment.getCourse().getId())) {
            throw new BadRequestException("You are not enrolled in this course");
        }

        // Check for existing submission
        if (submissionRepository.existsByAssignmentIdAndStudentId(assignmentId, studentId)) {
            throw new BadRequestException("You have already submitted this assignment");
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(request.getContent());
        submission.setFileUrl(request.getFileUrl());

        // Check if late
        if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
            submission.setStatus(SubmissionStatus.LATE);
        }

        return submissionRepository.save(submission);
    }

    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId).stream()
                .map(SubmissionResponse::fromEntity)
                .toList();
    }

    public SubmissionResponse getSubmission(Long assignmentId, Long studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .map(SubmissionResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
    }

    @Transactional
    public SubmissionResponse gradeSubmission(Long submissionId, GradeSubmissionRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId));

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setGradedAt(LocalDateTime.now());

        return SubmissionResponse.fromEntity(submissionRepository.save(submission));
    }
}
