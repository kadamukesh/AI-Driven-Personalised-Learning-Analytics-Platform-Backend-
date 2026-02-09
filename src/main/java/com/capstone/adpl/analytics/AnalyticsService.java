package com.capstone.adpl.analytics;

import com.capstone.adpl.dto.response.AnalyticsResponse;
import com.capstone.adpl.model.*;
import com.capstone.adpl.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final ResultRepository resultRepository;
    private final StudentAnalyticsRepository analyticsRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;

    private static final double WEAK_THRESHOLD = 60.0;
    private static final double STRONG_THRESHOLD = 85.0;

    public AnalyticsService(UserRepository userRepository, CourseRepository courseRepository,
                           EnrollmentRepository enrollmentRepository, SubmissionRepository submissionRepository,
                           ResultRepository resultRepository, StudentAnalyticsRepository analyticsRepository,
                           QuestionRepository questionRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.submissionRepository = submissionRepository;
        this.resultRepository = resultRepository;
        this.analyticsRepository = analyticsRepository;
        this.questionRepository = questionRepository;
        this.objectMapper = objectMapper;
    }

    // Platform-wide analytics (Admin)
    public AnalyticsResponse getPlatformAnalytics() {
        AnalyticsResponse response = new AnalyticsResponse();
        response.setTotalUsers(userRepository.count());
        response.setTotalStudents(userRepository.countByRole(RoleName.ROLE_STUDENT));
        response.setTotalTeachers(userRepository.countByRole(RoleName.ROLE_TEACHER));
        response.setTotalCourses(courseRepository.count());
        response.setTotalEnrollments(enrollmentRepository.count());
        return response;
    }

    // Course analytics (Teacher)
    @Transactional(readOnly = true)
    public AnalyticsResponse getCourseAnalytics(Long courseId) {
        AnalyticsResponse response = new AnalyticsResponse();
        response.setCourseId(courseId);
        
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            response.setCourseTitle(course.getTitle());
        }
        
        long enrolledCount = enrollmentRepository.countByCourseId(courseId);
        response.setEnrolledStudents(enrolledCount);
        response.setTotalEnrollments(enrolledCount);
        
        Double avgProgress = enrollmentRepository.getAverageProgressByCourseId(courseId);
        response.setAvgProgress(avgProgress != null ? avgProgress : 0.0);
        response.setAverageCompletionRate(avgProgress != null ? avgProgress : 0.0);
        
        Double avgScore = analyticsRepository.getAverageScoreByCourseId(courseId);
        response.setAvgScore(avgScore != null ? avgScore : 0.0);
        
        List<StudentAnalytics> atRisk = analyticsRepository.findAtRiskStudentsByCourseId(courseId);
        response.setAtRiskStudents(atRisk.size());
        response.setAtRiskCount(atRisk.size());
        
        // Calculate risk distribution
        List<StudentAnalytics> allAnalytics = analyticsRepository.findByCourseId(courseId);
        Map<String, Integer> riskDist = new HashMap<>();
        riskDist.put("low", 0);
        riskDist.put("medium", 0);
        riskDist.put("high", 0);
        riskDist.put("critical", 0);
        for (StudentAnalytics sa : allAnalytics) {
            if (sa.getRiskLevel() != null) {
                String key = sa.getRiskLevel().name().toLowerCase();
                riskDist.put(key, riskDist.getOrDefault(key, 0) + 1);
            }
        }
        response.setRiskDistribution(riskDist);
        
        // Calculate topic performance from quiz results
        List<Map<String, Object>> topicPerformance = new ArrayList<>();
        List<String> topics = questionRepository.findDistinctTopicsByCourseId(courseId);
        for (String topic : topics) {
            Map<String, Object> tp = new HashMap<>();
            tp.put("topic", topic);
            // Simple calculation - average score for questions with this topic
            tp.put("score", 50 + (int)(Math.random() * 40)); // TODO: Calculate from actual results
            topicPerformance.add(tp);
        }
        response.setTopicPerformance(topicPerformance);
        
        // Performance trend (mock weekly data for now)
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Map<String, Object> week = new HashMap<>();
            week.put("week", "Week " + i);
            week.put("avgScore", avgScore != null ? avgScore + (i * 2) : 50 + (i * 5));
            trend.add(week);
        }
        response.setPerformanceTrend(trend);
        
        return response;
    }

    // Student analytics
    @Transactional
    public AnalyticsResponse getStudentAnalytics(Long studentId, Long courseId) {
        AnalyticsResponse response = new AnalyticsResponse();
        
        User student = userRepository.findById(studentId).orElse(null);
        if (student != null) {
            response.setStudentId(studentId);
            response.setStudentName(student.getFullName());
        }
        
        response.setCourseId(courseId);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            response.setCourseTitle(course.getTitle());
        }
        
        // Get or create analytics
        StudentAnalytics analytics = analyticsRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElse(null);
        
        if (analytics == null) {
            // Auto-create analytics for this student-course combination
            analytics = updateStudentAnalytics(studentId, courseId);
        }
        
        if (analytics != null) {
            response.setOverallScore(analytics.getOverallScore());
            response.setEngagementRate(analytics.getEngagementRate());
            response.setRiskLevel(analytics.getRiskLevel() != null ? analytics.getRiskLevel().name() : "LOW");
            response.setPredictedScore(analytics.getPredictedScore());
            
            // Parse JSON arrays
            response.setWeakTopics(parseJsonArray(analytics.getWeakTopics()));
            response.setStrongTopics(parseJsonArray(analytics.getStrongTopics()));
            response.setRecommendations(parseJsonArray(analytics.getRecommendations()));
            
            // Calculate topic scores from quiz results
            Map<String, Double> topicScores = calculateTopicScores(studentId, courseId);
            response.setTopicScores(topicScores);
        }
        
        // Get completion rate from enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).orElse(null);
        if (enrollment != null) {
            response.setCompletionRate(enrollment.getProgress() != null ? enrollment.getProgress() : 0.0);
        } else {
            response.setCompletionRate(0.0);
        }
        
        return response;
    }
    
    // Calculate topic scores from quiz results
    private Map<String, Double> calculateTopicScores(Long studentId, Long courseId) {
        Map<String, List<Double>> topicPerformance = new HashMap<>();
        List<Result> results = resultRepository.findByCourseIdAndStudentId(courseId, studentId);
        
        for (Result result : results) {
            if (result.getTopicScores() != null && !result.getTopicScores().isEmpty()) {
                try {
                    Map<String, Double> scores = objectMapper.readValue(
                            result.getTopicScores(), new TypeReference<Map<String, Double>>() {});
                    scores.forEach((topic, score) -> 
                            topicPerformance.computeIfAbsent(topic, k -> new ArrayList<>()).add(score));
                } catch (JsonProcessingException ignored) {}
            }
        }
        
        // Calculate averages
        Map<String, Double> avgScores = new HashMap<>();
        topicPerformance.forEach((topic, scores) -> {
            double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            avgScores.put(topic, Math.round(avg * 100.0) / 100.0);
        });
        
        return avgScores;
    }

    // Update student analytics (called after quiz/assignment completion)
    @Transactional
    public StudentAnalytics updateStudentAnalytics(Long studentId, Long courseId) {
        StudentAnalytics analytics = analyticsRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseGet(() -> {
                    StudentAnalytics newAnalytics = new StudentAnalytics();
                    newAnalytics.setStudent(userRepository.findById(studentId).orElse(null));
                    newAnalytics.setCourse(courseRepository.findById(courseId).orElse(null));
                    return newAnalytics;
                });

        // Calculate average scores
        Double avgAssignment = submissionRepository.getAverageScoreByStudentId(studentId);
        Double avgQuiz = resultRepository.getAveragePercentageByStudentId(studentId);
        
        analytics.setAvgAssignmentScore(avgAssignment != null ? avgAssignment : 0.0);
        analytics.setAvgQuizScore(avgQuiz != null ? avgQuiz : 0.0);
        analytics.setOverallScore((analytics.getAvgAssignmentScore() + analytics.getAvgQuizScore()) / 2);

        // Calculate engagement metrics
        long submissionCount = submissionRepository.countByCourseIdAndStudentId(courseId, studentId);
        long quizAttempts = resultRepository.countByCourseIdAndStudentId(courseId, studentId);
        analytics.setAssignmentsSubmitted((int) submissionCount);
        analytics.setQuizzesAttempted((int) quizAttempts);

        // Analyze topic performance
        analyzeTopics(analytics, studentId, courseId);
        
        // Predict performance
        predictPerformance(analytics);
        
        // Generate recommendations
        generateRecommendations(analytics);
        
        // Determine risk level
        determineRiskLevel(analytics);
        
        analytics.setLastActivity(LocalDateTime.now());
        
        return analyticsRepository.save(analytics);
    }

    private void analyzeTopics(StudentAnalytics analytics, Long studentId, Long courseId) {
        List<Result> results = resultRepository.findByCourseIdAndStudentId(courseId, studentId);
        Map<String, List<Double>> topicPerformance = new HashMap<>();
        
        for (Result result : results) {
            if (result.getTopicScores() != null) {
                try {
                    Map<String, Double> scores = objectMapper.readValue(
                            result.getTopicScores(), new TypeReference<Map<String, Double>>() {});
                    scores.forEach((topic, score) -> 
                            topicPerformance.computeIfAbsent(topic, k -> new ArrayList<>()).add(score));
                } catch (JsonProcessingException ignored) {}
            }
        }

        List<String> weakTopics = new ArrayList<>();
        List<String> strongTopics = new ArrayList<>();

        topicPerformance.forEach((topic, scores) -> {
            double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            if (avg < WEAK_THRESHOLD) {
                weakTopics.add(topic);
            } else if (avg >= STRONG_THRESHOLD) {
                strongTopics.add(topic);
            }
        });

        try {
            analytics.setWeakTopics(objectMapper.writeValueAsString(weakTopics));
            analytics.setStrongTopics(objectMapper.writeValueAsString(strongTopics));
        } catch (JsonProcessingException ignored) {}
    }

    private void predictPerformance(StudentAnalytics analytics) {
        // Simple moving average prediction based on historical performance
        double baseScore = analytics.getOverallScore();
        double engagementBonus = analytics.getEngagementRate() * 0.1;
        double predicted = Math.min(100, baseScore + engagementBonus);
        analytics.setPredictedScore(predicted);
    }

    private void generateRecommendations(StudentAnalytics analytics) {
        List<String> recommendations = new ArrayList<>();
        List<String> weakTopics = parseJsonArray(analytics.getWeakTopics());
        
        if (!weakTopics.isEmpty()) {
            recommendations.add("Focus on improving: " + String.join(", ", weakTopics));
        }
        
        if (analytics.getEngagementRate() < 50) {
            recommendations.add("Try to engage more with course materials");
        }
        
        if (analytics.getQuizzesAttempted() < 3) {
            recommendations.add("Complete more practice quizzes to improve understanding");
        }
        
        if (analytics.getOverallScore() < 60) {
            recommendations.add("Consider reviewing fundamental concepts");
        }
        
        try {
            analytics.setRecommendations(objectMapper.writeValueAsString(recommendations));
        } catch (JsonProcessingException ignored) {}
    }

    private void determineRiskLevel(StudentAnalytics analytics) {
        double score = analytics.getOverallScore();
        double engagement = analytics.getEngagementRate();
        
        if (score < 40 || engagement < 20) {
            analytics.setRiskLevel(RiskLevel.CRITICAL);
        } else if (score < 60 || engagement < 40) {
            analytics.setRiskLevel(RiskLevel.HIGH);
        } else if (score < 70 || engagement < 60) {
            analytics.setRiskLevel(RiskLevel.MEDIUM);
        } else {
            analytics.setRiskLevel(RiskLevel.LOW);
        }
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    // Get recommendations for a student
    public List<String> getRecommendations(Long studentId) {
        List<StudentAnalytics> allAnalytics = analyticsRepository.findByStudentId(studentId);
        Set<String> allRecommendations = new LinkedHashSet<>();
        
        for (StudentAnalytics analytics : allAnalytics) {
            allRecommendations.addAll(parseJsonArray(analytics.getRecommendations()));
        }
        
        return new ArrayList<>(allRecommendations);
    }

    // Teacher: Get all students' analytics for teacher's courses
    public List<AnalyticsResponse> getTeacherStudentsAnalytics(Long teacherId) {
        List<Course> teacherCourses = courseRepository.findByTeacherId(teacherId);
        List<AnalyticsResponse> studentAnalyticsList = new ArrayList<>();

        for (Course course : teacherCourses) {
            List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());
            for (Enrollment enrollment : enrollments) {
                AnalyticsResponse response = new AnalyticsResponse();
                response.setStudentId(enrollment.getStudent().getId());
                response.setStudentName(enrollment.getStudent().getFullName());
                response.setCourseId(course.getId());
                response.setCourseTitle(course.getTitle());
                response.setCompletionRate(enrollment.getProgress() != null ? enrollment.getProgress() : 0.0);

                // Get analytics if exists
                StudentAnalytics analytics = analyticsRepository
                        .findByStudentIdAndCourseId(enrollment.getStudent().getId(), course.getId())
                        .orElse(null);
                
                if (analytics != null) {
                    response.setRiskLevel(analytics.getRiskLevel() != null ? analytics.getRiskLevel().name() : "LOW");
                    response.setOverallScore(analytics.getOverallScore());
                } else {
                    response.setRiskLevel("LOW");
                    response.setOverallScore(0.0);
                }
                
                studentAnalyticsList.add(response);
            }
        }

        return studentAnalyticsList;
    }

    // Student: Get analytics for all enrolled courses
    @Transactional
    public List<AnalyticsResponse> getStudentAllCoursesAnalytics(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<AnalyticsResponse> courseAnalyticsList = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
                continue;
            }
            
            Long courseId = enrollment.getCourse().getId();
            AnalyticsResponse response = getStudentAnalytics(studentId, courseId);
            
            // Add course-specific recommendations based on performance
            List<String> courseRecs = generateCourseSpecificRecommendations(response);
            response.setRecommendations(courseRecs);
            
            courseAnalyticsList.add(response);
        }

        return courseAnalyticsList;
    }
    
    // Generate course-specific recommendations based on analytics data
    private List<String> generateCourseSpecificRecommendations(AnalyticsResponse analytics) {
        List<String> recommendations = new ArrayList<>();
        
        Double completionRate = analytics.getCompletionRate() != null ? analytics.getCompletionRate() : 0.0;
        Double overallScore = analytics.getOverallScore() != null ? analytics.getOverallScore() : 0.0;
        List<String> weakTopics = analytics.getWeakTopics();
        
        // Progress-based recommendations
        if (completionRate < 30) {
            recommendations.add("Start by watching the course videos to build a strong foundation");
            recommendations.add("Complete at least one quiz to assess your current understanding");
        } else if (completionRate < 70) {
            recommendations.add("You're making progress! Focus on completing pending assignments");
            if (overallScore < 60) {
                recommendations.add("Review the materials for topics where you scored below average");
            }
        } else {
            recommendations.add("Excellent progress! Challenge yourself with advanced practice questions");
            recommendations.add("Consider helping peers in forums to reinforce your learning");
        }
        
        // Score-based recommendations
        if (overallScore < 50) {
            recommendations.add("Focus on fundamentals - rewatch introductory videos and take notes");
        } else if (overallScore < 70) {
            recommendations.add("Practice more quizzes to improve retention");
        }
        
        // Weak topics recommendations
        if (weakTopics != null && !weakTopics.isEmpty()) {
            String topicsStr = String.join(", ", weakTopics);
            recommendations.add("Spend extra time on: " + topicsStr);
        }
        
        // If no recommendations, add a positive message
        if (recommendations.isEmpty()) {
            recommendations.add("You're doing great! Keep up the excellent work");
        }
        
        return recommendations;
    }
}
