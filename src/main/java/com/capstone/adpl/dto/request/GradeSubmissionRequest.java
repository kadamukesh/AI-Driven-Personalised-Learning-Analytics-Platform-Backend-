package com.capstone.adpl.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class GradeSubmissionRequest {

    @Min(value = 0, message = "Score cannot be negative")
    private Integer score;

    private String feedback;

    public GradeSubmissionRequest() {
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
