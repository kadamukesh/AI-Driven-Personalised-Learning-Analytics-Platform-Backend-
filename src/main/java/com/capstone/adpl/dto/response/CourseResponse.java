package com.capstone.adpl.dto.response;

import com.capstone.adpl.model.Course;
import com.capstone.adpl.model.CourseMaterial;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String category;
    private String difficulty;
    private boolean published;
    private Long teacherId;
    private String teacherName;
    private int enrollmentCount;
    private int materialCount;
    private int assignmentCount;
    private int quizCount;
    private LocalDateTime createdAt;
    private List<CourseMaterialResponse> materials;

    public CourseResponse() {
    }

    public static CourseResponse fromEntity(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setThumbnailUrl(course.getThumbnailUrl());
        response.setCategory(course.getCategory());
        response.setDifficulty(course.getDifficulty().name());
        response.setPublished(course.isPublished());
        response.setTeacherId(course.getTeacher().getId());
        response.setTeacherName(course.getTeacher().getFullName());
        response.setEnrollmentCount(course.getEnrollments() != null ? course.getEnrollments().size() : 0);
        response.setMaterialCount(course.getMaterials() != null ? course.getMaterials().size() : 0);
        response.setAssignmentCount(course.getAssignments() != null ? course.getAssignments().size() : 0);
        response.setQuizCount(course.getQuizzes() != null ? course.getQuizzes().size() : 0);
        response.setCreatedAt(course.getCreatedAt());
        
        // Map materials to response list
        if (course.getMaterials() != null && !course.getMaterials().isEmpty()) {
            response.setMaterials(course.getMaterials().stream()
                    .map(CourseMaterialResponse::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            response.setMaterials(new ArrayList<>());
        }
        
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(int enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public int getMaterialCount() {
        return materialCount;
    }

    public void setMaterialCount(int materialCount) {
        this.materialCount = materialCount;
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }

    public void setAssignmentCount(int assignmentCount) {
        this.assignmentCount = assignmentCount;
    }

    public int getQuizCount() {
        return quizCount;
    }

    public void setQuizCount(int quizCount) {
        this.quizCount = quizCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CourseMaterialResponse> getMaterials() {
        return materials;
    }

    public void setMaterials(List<CourseMaterialResponse> materials) {
        this.materials = materials;
    }
}
