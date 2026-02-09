package com.capstone.adpl.service;

import com.capstone.adpl.dto.request.QuestionRequest;
import com.capstone.adpl.dto.response.QuestionResponse;
import com.capstone.adpl.exception.ResourceNotFoundException;
import com.capstone.adpl.model.Question;
import com.capstone.adpl.model.Quiz;
import com.capstone.adpl.model.QuestionType;
import com.capstone.adpl.repository.QuestionRepository;
import com.capstone.adpl.repository.QuizRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper;

    public QuestionService(QuestionRepository questionRepository, QuizRepository quizRepository, ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.objectMapper = objectMapper;
    }

    public List<QuestionResponse> getQuestionsByQuiz(Long quizId) {
        return questionRepository.findByQuizIdOrderByOrderIndexAsc(quizId).stream()
                .map(QuestionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public QuestionResponse getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        return QuestionResponse.fromEntity(question);
    }

    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", request.getQuizId()));

        Question question = new Question();
        question.setQuestionText(request.getQuestionText());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setExplanation(request.getExplanation());
        question.setMarks(request.getMarks() != null ? request.getMarks() : 1);
        question.setOrderIndex(request.getOrderIndex());
        question.setTopicTag(request.getTopicTag());
        question.setQuiz(quiz);

        if (request.getQuestionType() != null) {
            try {
                question.setQuestionType(QuestionType.valueOf(request.getQuestionType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
            }
        } else {
            question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        }

        if (request.getOptions() != null) {
            try {
                question.setOptions(objectMapper.writeValueAsString(request.getOptions()));
            } catch (JsonProcessingException e) {
                question.setOptions("[]");
            }
        }

        Question saved = questionRepository.save(question);
        return QuestionResponse.fromEntity(saved);
    }

    @Transactional
    public QuestionResponse updateQuestion(Long id, QuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        if (request.getQuestionText() != null) {
            question.setQuestionText(request.getQuestionText());
        }
        if (request.getCorrectAnswer() != null) {
            question.setCorrectAnswer(request.getCorrectAnswer());
        }
        if (request.getExplanation() != null) {
            question.setExplanation(request.getExplanation());
        }
        if (request.getMarks() != null) {
            question.setMarks(request.getMarks());
        }
        if (request.getOrderIndex() != null) {
            question.setOrderIndex(request.getOrderIndex());
        }
        if (request.getTopicTag() != null) {
            question.setTopicTag(request.getTopicTag());
        }
        if (request.getQuestionType() != null) {
            try {
                question.setQuestionType(QuestionType.valueOf(request.getQuestionType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Keep existing type
            }
        }
        if (request.getOptions() != null) {
            try {
                question.setOptions(objectMapper.writeValueAsString(request.getOptions()));
            } catch (JsonProcessingException e) {
                // Keep existing options
            }
        }

        Question saved = questionRepository.save(question);
        return QuestionResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question", "id", id);
        }
        questionRepository.deleteById(id);
    }
}
