package com.capstone.adpl.dto.response;

import java.util.List;
import java.util.Map;

public class AnalyticsResponse {

    // Platform-level analytics
    private Long totalUsers;
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalCourses;
    private Long totalEnrollments;
    private Double averageCompletionRate;

    // Course-level analytics
    private Long courseId;
    private String courseTitle;
    private Long enrolledStudents;
    private Double avgProgress;
    private Double avgScore;
    private Integer atRiskStudents;

    // Student-level analytics
    private Long studentId;
    private String studentName;
    private Double overallScore;
    private Double engagementRate;
    private Double completionRate;
    private List<String> weakTopics;
    private List<String> strongTopics;
    private List<String> recommendations;
    private String riskLevel;
    private Double predictedScore;

    // Chart data
    private List<Map<String, Object>> performanceTrend;
    private List<Map<String, Object>> topicPerformance;
    private Map<String, Double> topicScores;
    private Map<String, Integer> riskDistribution;
    private Integer atRiskCount;

    public AnalyticsResponse() {
    }

    // All getters and setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getTotalTeachers() {
        return totalTeachers;
    }

    public void setTotalTeachers(Long totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public Long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Long totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Long getTotalEnrollments() {
        return totalEnrollments;
    }

    public void setTotalEnrollments(Long totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }

    public Double getAverageCompletionRate() {
        return averageCompletionRate;
    }

    public void setAverageCompletionRate(Double averageCompletionRate) {
        this.averageCompletionRate = averageCompletionRate;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Long getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(Long enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public Double getAvgProgress() {
        return avgProgress;
    }

    public void setAvgProgress(Double avgProgress) {
        this.avgProgress = avgProgress;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public Integer getAtRiskStudents() {
        return atRiskStudents;
    }

    public void setAtRiskStudents(Integer atRiskStudents) {
        this.atRiskStudents = atRiskStudents;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public List<String> getWeakTopics() {
        return weakTopics;
    }

    public void setWeakTopics(List<String> weakTopics) {
        this.weakTopics = weakTopics;
    }

    public List<String> getStrongTopics() {
        return strongTopics;
    }

    public void setStrongTopics(List<String> strongTopics) {
        this.strongTopics = strongTopics;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getPredictedScore() {
        return predictedScore;
    }

    public void setPredictedScore(Double predictedScore) {
        this.predictedScore = predictedScore;
    }

    public List<Map<String, Object>> getPerformanceTrend() {
        return performanceTrend;
    }

    public void setPerformanceTrend(List<Map<String, Object>> performanceTrend) {
        this.performanceTrend = performanceTrend;
    }

    public Map<String, Double> getTopicScores() {
        return topicScores;
    }

    public void setTopicScores(Map<String, Double> topicScores) {
        this.topicScores = topicScores;
    }

    public List<Map<String, Object>> getTopicPerformance() {
        return topicPerformance;
    }

    public void setTopicPerformance(List<Map<String, Object>> topicPerformance) {
        this.topicPerformance = topicPerformance;
    }

    public Map<String, Integer> getRiskDistribution() {
        return riskDistribution;
    }

    public void setRiskDistribution(Map<String, Integer> riskDistribution) {
        this.riskDistribution = riskDistribution;
    }

    public Integer getAtRiskCount() {
        return atRiskCount;
    }

    public void setAtRiskCount(Integer atRiskCount) {
        this.atRiskCount = atRiskCount;
    }
}
