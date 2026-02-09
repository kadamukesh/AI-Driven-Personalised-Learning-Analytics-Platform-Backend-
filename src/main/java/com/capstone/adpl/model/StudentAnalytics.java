package com.capstone.adpl.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_analytics", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
public class StudentAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "avg_assignment_score")
    private Double avgAssignmentScore = 0.0;

    @Column(name = "avg_quiz_score")
    private Double avgQuizScore = 0.0;

    @Column(name = "overall_score")
    private Double overallScore = 0.0;

    @Column(name = "engagement_rate")
    private Double engagementRate = 0.0;

    @Column(name = "completion_rate")
    private Double completionRate = 0.0;

    @Column(name = "total_time_spent")
    private Integer totalTimeSpent = 0;  // in minutes

    @Column(name = "materials_viewed")
    private Integer materialsViewed = 0;

    @Column(name = "assignments_submitted")
    private Integer assignmentsSubmitted = 0;

    @Column(name = "quizzes_attempted")
    private Integer quizzesAttempted = 0;

    @Column(name = "weak_topics", columnDefinition = "TEXT")
    private String weakTopics;  // JSON array

    @Column(name = "strong_topics", columnDefinition = "TEXT")
    private String strongTopics;  // JSON array

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;  // JSON array

    @Column(name = "predicted_score")
    private Double predictedScore;

    @Column(name = "risk_level")
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel = RiskLevel.LOW;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public StudentAnalytics() {
    }

    public StudentAnalytics(User student, Course course) {
        this.student = student;
        this.course = course;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Double getAvgAssignmentScore() {
        return avgAssignmentScore;
    }

    public void setAvgAssignmentScore(Double avgAssignmentScore) {
        this.avgAssignmentScore = avgAssignmentScore;
    }

    public Double getAvgQuizScore() {
        return avgQuizScore;
    }

    public void setAvgQuizScore(Double avgQuizScore) {
        this.avgQuizScore = avgQuizScore;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public Double getEngagementRate() {
        return engagementRate;
    }

    public void setEngagementRate(Double engagementRate) {
        this.engagementRate = engagementRate;
    }

    public Double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
    }

    public Integer getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(Integer totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public Integer getMaterialsViewed() {
        return materialsViewed;
    }

    public void setMaterialsViewed(Integer materialsViewed) {
        this.materialsViewed = materialsViewed;
    }

    public Integer getAssignmentsSubmitted() {
        return assignmentsSubmitted;
    }

    public void setAssignmentsSubmitted(Integer assignmentsSubmitted) {
        this.assignmentsSubmitted = assignmentsSubmitted;
    }

    public Integer getQuizzesAttempted() {
        return quizzesAttempted;
    }

    public void setQuizzesAttempted(Integer quizzesAttempted) {
        this.quizzesAttempted = quizzesAttempted;
    }

    public String getWeakTopics() {
        return weakTopics;
    }

    public void setWeakTopics(String weakTopics) {
        this.weakTopics = weakTopics;
    }

    public String getStrongTopics() {
        return strongTopics;
    }

    public void setStrongTopics(String strongTopics) {
        this.strongTopics = strongTopics;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public Double getPredictedScore() {
        return predictedScore;
    }

    public void setPredictedScore(Double predictedScore) {
        this.predictedScore = predictedScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
