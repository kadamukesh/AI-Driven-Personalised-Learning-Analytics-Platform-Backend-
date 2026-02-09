package com.capstone.adpl.service;

import com.capstone.adpl.exception.BadRequestException;
import com.capstone.adpl.exception.ResourceNotFoundException;
import com.capstone.adpl.model.Course;
import com.capstone.adpl.model.Enrollment;
import com.capstone.adpl.model.EnrollmentStatus;
import com.capstone.adpl.model.User;
import com.capstone.adpl.repository.CourseRepository;
import com.capstone.adpl.repository.EnrollmentRepository;
import com.capstone.adpl.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Enrollment enrollStudent(Long studentId, Long courseId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new BadRequestException("Student is already enrolled in this course");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.isPublished()) {
            throw new BadRequestException("Cannot enroll in an unpublished course");
        }

        Enrollment enrollment = new Enrollment(student, course);
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public Enrollment getEnrollment(Long studentId, Long courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
    }

    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Transactional
    public Enrollment updateProgress(Long studentId, Long courseId, Double progress) {
        Enrollment enrollment = getEnrollment(studentId, courseId);
        enrollment.setProgress(progress);
        
        if (progress >= 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        }
        
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void dropCourse(Long studentId, Long courseId) {
        Enrollment enrollment = getEnrollment(studentId, courseId);
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }

    public long countEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    public Double getAverageProgress(Long courseId) {
        return enrollmentRepository.getAverageProgressByCourseId(courseId);
    }
}
