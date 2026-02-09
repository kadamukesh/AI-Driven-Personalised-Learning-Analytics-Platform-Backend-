package com.capstone.adpl.dto.request;

import java.util.Map;

public class QuizSubmitRequest {

    private Map<Long, String> answers; // questionId -> answer
    private Integer timeTaken; // in seconds

    public QuizSubmitRequest() {
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }
}
