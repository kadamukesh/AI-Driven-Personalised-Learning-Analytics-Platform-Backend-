package com.capstone.adpl.dto.response;

import com.capstone.adpl.model.Result;
import java.time.LocalDateTime;

public class ResultResponse {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private Long courseId;
    private String courseTitle;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Integer score;
    private Integer totalMarks;
    private Integer timeTaken;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer unanswered;
    private boolean passed;
    private Integer attemptNumber;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public static ResultResponse fromEntity(Result result) {
        ResultResponse response = new ResultResponse();
        response.setId(result.getId());
        response.setScore(result.getScore());
        response.setTotalMarks(result.getTotalMarks());
        response.setTimeTaken(result.getTimeTaken());
        response.setCorrectAnswers(result.getCorrectAnswers());
        response.setWrongAnswers(result.getWrongAnswers());
        response.setUnanswered(result.getUnanswered());
        response.setPassed(result.isPassed());
        response.setAttemptNumber(result.getAttemptNumber());
        response.setStartedAt(result.getStartedAt());
        response.setCompletedAt(result.getCompletedAt());
        
        if (result.getQuiz() != null) {
            response.setQuizId(result.getQuiz().getId());
            response.setQuizTitle(result.getQuiz().getTitle());
            if (result.getQuiz().getCourse() != null) {
                response.setCourseId(result.getQuiz().getCourse().getId());
                response.setCourseTitle(result.getQuiz().getCourse().getTitle());
            }
        }
        
        if (result.getStudent() != null) {
            response.setStudentId(result.getStudent().getId());
            response.setStudentName(result.getStudent().getFullName());
            response.setStudentEmail(result.getStudent().getEmail());
        }
        
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }
    public Integer getTimeTaken() { return timeTaken; }
    public void setTimeTaken(Integer timeTaken) { this.timeTaken = timeTaken; }
    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }
    public Integer getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(Integer wrongAnswers) { this.wrongAnswers = wrongAnswers; }
    public Integer getUnanswered() { return unanswered; }
    public void setUnanswered(Integer unanswered) { this.unanswered = unanswered; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public Integer getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(Integer attemptNumber) { this.attemptNumber = attemptNumber; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
