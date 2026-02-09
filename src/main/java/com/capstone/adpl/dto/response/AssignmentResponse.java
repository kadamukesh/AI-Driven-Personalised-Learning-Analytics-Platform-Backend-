package com.capstone.adpl.dto.response;

import com.capstone.adpl.model.Assignment;
import java.time.LocalDateTime;

public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private String instructions;
    private Long courseId;
    private String courseTitle;
    private String teacherName;
    private LocalDateTime dueDate;
    private Integer maxScore;
    private String topicTags;
    private String materialUrl;
    private String materialType;
    private int submissionCount;
    private Integer score;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssignmentResponse() {}

    public static AssignmentResponse fromEntity(Assignment assignment) {
        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setTitle(assignment.getTitle());
        response.setDescription(assignment.getDescription());
        response.setInstructions(assignment.getInstructions());
        if (assignment.getCourse() != null) {
            response.setCourseId(assignment.getCourse().getId());
            response.setCourseTitle(assignment.getCourse().getTitle());
            if (assignment.getCourse().getTeacher() != null) {
                response.setTeacherName(assignment.getCourse().getTeacher().getFirstName() + " " + assignment.getCourse().getTeacher().getLastName());
            }
        }
        response.setDueDate(assignment.getDueDate());
        response.setMaxScore(assignment.getMaxScore());
        response.setTopicTags(assignment.getTopicTags());
        response.setMaterialUrl(assignment.getMaterialUrl());
        response.setMaterialType(assignment.getMaterialType());
        response.setSubmissionCount(assignment.getSubmissions() != null ? assignment.getSubmissions().size() : 0);
        response.setCreatedAt(assignment.getCreatedAt());
        response.setUpdatedAt(assignment.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }
    public String getTopicTags() { return topicTags; }
    public void setTopicTags(String topicTags) { this.topicTags = topicTags; }
    public String getMaterialUrl() { return materialUrl; }
    public void setMaterialUrl(String materialUrl) { this.materialUrl = materialUrl; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public int getSubmissionCount() { return submissionCount; }
    public void setSubmissionCount(int submissionCount) { this.submissionCount = submissionCount; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
