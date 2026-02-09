package com.capstone.adpl.service;

import com.capstone.adpl.dto.request.QuestionRequest;
import com.capstone.adpl.dto.request.QuizRequest;
import com.capstone.adpl.dto.request.QuizSubmitRequest;
import com.capstone.adpl.dto.response.QuizResponse;
import com.capstone.adpl.exception.BadRequestException;
import com.capstone.adpl.exception.ResourceNotFoundException;
import com.capstone.adpl.model.*;
import com.capstone.adpl.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(QuizService.class);

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ObjectMapper objectMapper;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository,
                       ResultRepository resultRepository, CourseRepository courseRepository,
                       UserRepository userRepository, EnrollmentRepository enrollmentRepository,
                       ObjectMapper objectMapper) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.objectMapper = objectMapper;
    }

    public List<QuizResponse> getQuizzesByTeacher(Long teacherId) {
        return quizRepository.findByTeacherId(teacherId).stream()
                .map(QuizResponse::fromEntity)
                .toList();
    }

    public List<QuizResponse> getQuizzesByCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId).stream()
                .map(QuizResponse::fromEntity)
                .toList();
    }

    public List<QuizResponse> getAvailableQuizzes(Long courseId) {
        return quizRepository.findAvailableQuizzes(courseId, LocalDateTime.now()).stream()
                .map(QuizResponse::fromEntity)
                .toList();
    }

    public List<QuizResponse> getAvailableQuizzesForStudent(Long studentId) {
        return quizRepository.findAllAvailableQuizzesForStudent(studentId, LocalDateTime.now()).stream()
                .map(QuizResponse::fromEntity)
                .toList();
    }

    public QuizResponse getQuizResponseById(Long id) {
        return QuizResponse.fromEntity(getQuizById(id));
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Question> getQuizQuestions(Long quizId) {
        Quiz quiz = getQuizById(quizId);
        List<Question> questions = questionRepository.findByQuizIdOrderByOrderIndexAsc(quizId);
        
        if (quiz.isRandomize()) {
            Collections.shuffle(questions);
        }
        
        return questions;
    }

    @Transactional
    public QuizResponse createQuiz(QuizRequest request, Long teacherId) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new BadRequestException("You are not authorized to create quizzes for this course");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setCourse(course);
        quiz.setDurationMinutes(request.getDurationMinutes());
        quiz.setTotalMarks(request.getTotalMarks());
        quiz.setPassingMarks(request.getPassingMarks());
        quiz.setRandomize(request.isRandomize());
        quiz.setShowAnswers(request.isShowAnswers());
        quiz.setMaxAttempts(request.getMaxAttempts());
        quiz.setTopicTags(request.getTopicTags());
        quiz.setPublished(request.isPublished());
        quiz.setStartTime(request.getStartTime());
        quiz.setEndTime(request.getEndTime());

        Quiz savedQuiz = quizRepository.save(quiz);

        // Add questions if provided
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            int index = 0;
            for (QuestionRequest qr : request.getQuestions()) {
                Question question = new Question();
                question.setQuestionText(qr.getQuestionText());
                question.setQuestionType(QuestionType.valueOf(qr.getQuestionType().toUpperCase()));
                question.setCorrectAnswer(qr.getCorrectAnswer());
                question.setExplanation(qr.getExplanation());
                question.setMarks(qr.getMarks() != null ? qr.getMarks() : 1);
                question.setOrderIndex(qr.getOrderIndex() != null ? qr.getOrderIndex() : index++);
                question.setTopicTag(qr.getTopicTag());
                
                if (qr.getOptions() != null) {
                    try {
                        question.setOptions(objectMapper.writeValueAsString(qr.getOptions()));
                    } catch (JsonProcessingException e) {
                        question.setOptions("[]");
                    }
                }
                
                savedQuiz.addQuestion(question);
            }
            savedQuiz = quizRepository.save(savedQuiz);
        }

        return QuizResponse.fromEntity(savedQuiz);
    }

    @Transactional
    public QuizResponse updateQuiz(Long id, QuizRequest request, Long teacherId) {
        Quiz quiz = getQuizById(id);

        if (!quiz.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new BadRequestException("You are not authorized to update this quiz");
        }

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setDurationMinutes(request.getDurationMinutes());
        quiz.setTotalMarks(request.getTotalMarks());
        quiz.setPassingMarks(request.getPassingMarks());
        quiz.setRandomize(request.isRandomize());
        quiz.setShowAnswers(request.isShowAnswers());
        quiz.setMaxAttempts(request.getMaxAttempts());
        quiz.setTopicTags(request.getTopicTags());
        quiz.setPublished(request.isPublished());
        quiz.setStartTime(request.getStartTime());
        quiz.setEndTime(request.getEndTime());

        return QuizResponse.fromEntity(quizRepository.save(quiz));
    }

    @Transactional
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quiz", "id", id);
        }
        quizRepository.deleteById(id);
    }

    @Transactional
    public Result submitQuiz(Long quizId, Long studentId, QuizSubmitRequest request) {
        Quiz quiz = getQuizById(quizId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        // Check enrollment
        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(studentId, quiz.getCourse().getId());
        log.info("Enrollment check: studentId={}, courseId={}, isEnrolled={}", studentId, quiz.getCourse().getId(), isEnrolled);
        if (!isEnrolled) {
            throw new BadRequestException("You are not enrolled in this course");
        }

        // Check attempts
        int attempts = resultRepository.countAttemptsByQuizAndStudent(quizId, studentId);
        log.info("Attempts check: attempts={}, maxAttempts={}", attempts, quiz.getMaxAttempts());
        if (attempts >= quiz.getMaxAttempts()) {
            throw new BadRequestException("You have exceeded the maximum number of attempts");
        }

        // Calculate score
        List<Question> questions = questionRepository.findByQuizIdOrderByOrderIndexAsc(quizId);
        int score = 0;
        int correctAnswers = 0;
        int wrongAnswers = 0;
        int unanswered = 0;
        Map<String, Double> topicScores = new HashMap<>();

        for (Question question : questions) {
            String studentAnswer = request.getAnswers().get(question.getId());
            
            if (studentAnswer == null || studentAnswer.isEmpty()) {
                unanswered++;
            } else if (studentAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                score += question.getMarks();
                correctAnswers++;
                
                // Track topic scores
                if (question.getTopicTag() != null) {
                    topicScores.merge(question.getTopicTag(), (double) question.getMarks(), Double::sum);
                }
            } else {
                wrongAnswers++;
            }
        }

        Result result = new Result();
        result.setQuiz(quiz);
        result.setStudent(student);
        result.setScore(score);
        result.setTotalMarks(quiz.getTotalMarks());
        result.setTimeTaken(request.getTimeTaken());
        result.setCorrectAnswers(correctAnswers);
        result.setWrongAnswers(wrongAnswers);
        result.setUnanswered(unanswered);
        result.setPassed(score >= quiz.getPassingMarks());
        result.setAttemptNumber(attempts + 1);
        result.setStartedAt(LocalDateTime.now().minusSeconds(request.getTimeTaken() != null ? request.getTimeTaken() : 0));
        result.setCompletedAt(LocalDateTime.now());

        try {
            result.setAnswersJson(objectMapper.writeValueAsString(request.getAnswers()));
            result.setTopicScores(objectMapper.writeValueAsString(topicScores));
        } catch (JsonProcessingException e) {
            result.setAnswersJson("{}");
            result.setTopicScores("{}");
        }

        return resultRepository.save(result);
    }


    @Transactional(readOnly = true)
    public List<Result> getQuizResults(Long quizId) {
        return resultRepository.findByQuizId(quizId);
    }

    @Transactional(readOnly = true)
    public List<Result> getStudentResults(Long studentId) {
        return resultRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public Result getBestResult(Long quizId, Long studentId) {
        return resultRepository.findTopByQuizIdAndStudentIdOrderByScoreDesc(quizId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("No results found"));
    }
}
