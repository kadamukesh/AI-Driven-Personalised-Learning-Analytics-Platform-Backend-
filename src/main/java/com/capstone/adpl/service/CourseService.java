package com.capstone.adpl.service;

import com.capstone.adpl.dto.request.CourseRequest;
import com.capstone.adpl.dto.response.CourseResponse;
import com.capstone.adpl.exception.BadRequestException;
import com.capstone.adpl.exception.ResourceNotFoundException;
import com.capstone.adpl.model.Course;
import com.capstone.adpl.model.DifficultyLevel;
import com.capstone.adpl.model.User;
import com.capstone.adpl.repository.CourseRepository;
import com.capstone.adpl.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getPublishedCourses() {
        return courseRepository.findByPublishedTrue().stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        return CourseResponse.fromEntity(course);
    }

    public List<CourseResponse> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> searchCourses(String keyword) {
        return courseRepository.searchCourses(keyword).stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return courseRepository.findAllCategories();
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", teacherId));

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setCategory(request.getCategory());
        course.setTeacher(teacher);
        course.setPublished(request.isPublished());

        if (request.getDifficulty() != null) {
            try {
                course.setDifficulty(DifficultyLevel.valueOf(request.getDifficulty().toUpperCase()));
            } catch (IllegalArgumentException e) {
                course.setDifficulty(DifficultyLevel.BEGINNER);
            }
        }

        Course savedCourse = courseRepository.save(course);
        return CourseResponse.fromEntity(savedCourse);
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getThumbnailUrl() != null) {
            course.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getCategory() != null) {
            course.setCategory(request.getCategory());
        }
        if (request.getDifficulty() != null) {
            try {
                course.setDifficulty(DifficultyLevel.valueOf(request.getDifficulty().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Keep existing difficulty
            }
        }
        course.setPublished(request.isPublished());

        Course savedCourse = courseRepository.save(course);
        return CourseResponse.fromEntity(savedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course", "id", id);
        }
        courseRepository.deleteById(id);
    }

    @Transactional
    public CourseResponse togglePublish(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        course.setPublished(!course.isPublished());
        Course savedCourse = courseRepository.save(course);
        return CourseResponse.fromEntity(savedCourse);
    }

    public long countCourses() {
        return courseRepository.count();
    }

    public long countPublishedCourses() {
        return courseRepository.countPublished();
    }
}
