package com.capstone.adpl.dto.response;

import com.capstone.adpl.model.Question;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class QuestionResponse {

    private Long id;
    private String questionText;
    private String questionType;
    private List<String> options;
    private String correctAnswer;
    private String explanation;
    private Integer marks;
    private Integer orderIndex;
    private String topicTag;
    private Long quizId;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static QuestionResponse fromEntity(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestionText(question.getQuestionText());
        response.setQuestionType(question.getQuestionType() != null ? question.getQuestionType().name() : null);
        response.setCorrectAnswer(question.getCorrectAnswer());
        response.setExplanation(question.getExplanation());
        response.setMarks(question.getMarks());
        response.setOrderIndex(question.getOrderIndex());
        response.setTopicTag(question.getTopicTag());
        if (question.getQuiz() != null) {
            response.setQuizId(question.getQuiz().getId());
        }

        if (question.getOptions() != null) {
            try {
                response.setOptions(objectMapper.readValue(question.getOptions(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                response.setOptions(List.of());
            }
        }

        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getTopicTag() {
        return topicTag;
    }

    public void setTopicTag(String topicTag) {
        this.topicTag = topicTag;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
}
