package com.capstone.adpl.dto.response;

import com.capstone.adpl.model.Quiz;
import java.time.LocalDateTime;

public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private String courseTitle;
    private Integer durationMinutes;
    private Integer totalMarks;
    private Integer passingMarks;
    private boolean published;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int questionCount;
    private LocalDateTime createdAt;

    public QuizResponse() {}

    public static QuizResponse fromEntity(Quiz quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        if (quiz.getCourse() != null) {
            response.setCourseId(quiz.getCourse().getId());
            response.setCourseTitle(quiz.getCourse().getTitle());
        }
        response.setDurationMinutes(quiz.getDurationMinutes());
        response.setTotalMarks(quiz.getTotalMarks());
        response.setPassingMarks(quiz.getPassingMarks());
        response.setPublished(quiz.isPublished());
        response.setStartTime(quiz.getStartTime());
        response.setEndTime(quiz.getEndTime());
        response.setQuestionCount(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0);
        response.setCreatedAt(quiz.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }
    public Integer getPassingMarks() { return passingMarks; }
    public void setPassingMarks(Integer passingMarks) { this.passingMarks = passingMarks; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
