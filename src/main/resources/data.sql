-- Sample Data for AI-Driven Personalized Learning Analytics Platform
-- This file seeds the database with initial data for testing

-- Note: Passwords are BCrypt encoded. The password for all users is: password123
-- BCrypt hash for 'password123': $2a$10$N9qo8uLOickgx2ZMRZoMy.MqV6f0Q3uXYqZ5pzFY7JzOmWvfLqGCG

-- Insert Roles (if not exists)
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_TEACHER') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (3, 'ROLE_STUDENT') ON CONFLICT (id) DO NOTHING;

-- Insert Admin User
INSERT INTO users (id, email, password, first_name, last_name, enabled, created_at, updated_at)
VALUES (1, 'admin@learnai.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqV6f0Q3uXYqZ5pzFY7JzOmWvfLqGCG', 'Admin', 'User', true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Insert Teacher User
INSERT INTO users (id, email, password, first_name, last_name, enabled, created_at, updated_at)
VALUES (2, 'teacher@learnai.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqV6f0Q3uXYqZ5pzFY7JzOmWvfLqGCG', 'John', 'Teacher', true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Insert Student User
INSERT INTO users (id, email, password, first_name, last_name, enabled, created_at, updated_at)
VALUES (3, 'student@learnai.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqV6f0Q3uXYqZ5pzFY7JzOmWvfLqGCG', 'Jane', 'Student', true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Assign Roles to Users
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2) ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3) ON CONFLICT DO NOTHING;

-- Insert Sample Courses (PUBLISHED)
INSERT INTO courses (id, title, description, category, difficulty, teacher_id, published, created_at)
VALUES 
(1, 'Introduction to Python Programming', 'Learn Python from scratch with hands-on projects and real-world examples.', 'CSE', 'BEGINNER', 2, true, NOW()),
(2, 'Data Structures and Algorithms', 'Master DSA concepts with Java including arrays, trees, graphs, and dynamic programming.', 'CSE', 'INTERMEDIATE', 2, true, NOW()),
(3, 'Machine Learning Fundamentals', 'Explore ML algorithms, neural networks, and practical applications using Python and TensorFlow.', 'CSE', 'ADVANCED', 2, true, NOW()),
(4, 'Web Development with React', 'Build modern web applications using React, Redux, and REST APIs.', 'CSE', 'INTERMEDIATE', 2, true, NOW()),
(5, 'Database Management Systems', 'Learn SQL, NoSQL, database design, and optimization techniques.', 'CSE', 'BEGINNER', 2, true, NOW())
ON CONFLICT (id) DO UPDATE SET published = true;

-- Insert Sample Assignments
INSERT INTO assignments (id, title, description, course_id, due_date, max_score, created_at)
VALUES 
(1, 'Python Basics Quiz', 'Complete the Python basics programming exercises', 1, NOW() + INTERVAL '7 days', 100, NOW()),
(2, 'Binary Search Tree Implementation', 'Implement a BST with insert, delete, and search operations', 2, NOW() + INTERVAL '14 days', 100, NOW()),
(3, 'Linear Regression Project', 'Build a linear regression model to predict housing prices', 3, NOW() + INTERVAL '21 days', 100, NOW()),
(4, 'React Todo App', 'Create a todo application using React hooks and context', 4, NOW() + INTERVAL '10 days', 100, NOW()),
(5, 'SQL Query Practice', 'Write SQL queries for the given database scenarios', 5, NOW() + INTERVAL '7 days', 100, NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert Sample Quizzes (PUBLISHED)
INSERT INTO quizzes (id, title, description, course_id, duration_minutes, total_marks, passing_marks, randomize, show_answers, max_attempts, published, created_at)
VALUES 
(1, 'Python Fundamentals Quiz', 'Test your knowledge of Python basics', 1, 30, 50, 25, false, true, 3, true, NOW()),
(2, 'DSA Concepts Quiz', 'Assessment on data structures and algorithms', 2, 45, 100, 50, true, true, 2, true, NOW()),
(3, 'ML Algorithms Quiz', 'Quiz on machine learning algorithms and concepts', 3, 60, 100, 60, true, false, 2, true, NOW()),
(4, 'React Basics Quiz', 'Test your understanding of React fundamentals', 4, 30, 50, 25, false, true, 3, true, NOW()),
(5, 'SQL Basics Quiz', 'Quiz on SQL queries and database concepts', 5, 30, 50, 25, false, true, 3, true, NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert Sample Questions for Python Quiz
INSERT INTO questions (id, quiz_id, question_text, question_type, options, correct_answer, marks, order_index, topic_tag)
VALUES 
(1, 1, 'What is the output of print(2 ** 3)?', 'MCQ', '["6", "8", "9", "5"]', '8', 10, 1, 'Operators'),
(2, 1, 'Which of the following is a mutable data type in Python?', 'MCQ', '["Tuple", "String", "List", "Integer"]', 'List', 10, 2, 'Data Types'),
(3, 1, 'What keyword is used to define a function in Python?', 'MCQ', '["function", "def", "fun", "define"]', 'def', 10, 3, 'Functions'),
(4, 1, 'Which method is used to add an element to a list?', 'MCQ', '["add()", "append()", "insert()", "push()"]', 'append()', 10, 4, 'Lists'),
(5, 1, 'What is the correct way to create a dictionary?', 'MCQ', '["dict = []", "dict = {}", "dict = ()", "dict = //"]', 'dict = {}', 10, 5, 'Dictionaries')
ON CONFLICT (id) DO NOTHING;

-- Insert Sample Questions for DSA Quiz
INSERT INTO questions (id, quiz_id, question_text, question_type, options, correct_answer, marks, order_index, topic_tag)
VALUES 
(6, 2, 'What is the time complexity of binary search?', 'MCQ', '["O(n)", "O(log n)", "O(n^2)", "O(1)"]', 'O(log n)', 20, 1, 'Searching'),
(7, 2, 'Which data structure uses LIFO principle?', 'MCQ', '["Queue", "Stack", "Linked List", "Tree"]', 'Stack', 20, 2, 'Stacks'),
(8, 2, 'What is the worst-case time complexity of Quick Sort?', 'MCQ', '["O(n log n)", "O(n)", "O(n^2)", "O(log n)"]', 'O(n^2)', 20, 3, 'Sorting'),
(9, 2, 'Which traversal visits the root node first?', 'MCQ', '["Inorder", "Preorder", "Postorder", "Level order"]', 'Preorder', 20, 4, 'Trees'),
(10, 2, 'What is the space complexity of merge sort?', 'MCQ', '["O(1)", "O(log n)", "O(n)", "O(n^2)"]', 'O(n)', 20, 5, 'Sorting')
ON CONFLICT (id) DO NOTHING;

-- Enroll the sample student in courses
INSERT INTO enrollments (id, student_id, course_id, progress, status, enrolled_at)
VALUES 
(1, 3, 1, 0.0, 'ACTIVE', NOW()),
(2, 3, 2, 0.0, 'ACTIVE', NOW()),
(3, 3, 4, 0.0, 'ACTIVE', NOW())
ON CONFLICT (id) DO NOTHING;

-- Update sequence values to avoid conflicts
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('courses_id_seq', (SELECT MAX(id) FROM courses));
SELECT setval('assignments_id_seq', (SELECT MAX(id) FROM assignments));
SELECT setval('quizzes_id_seq', (SELECT MAX(id) FROM quizzes));
SELECT setval('questions_id_seq', (SELECT MAX(id) FROM questions));
SELECT setval('enrollments_id_seq', (SELECT MAX(id) FROM enrollments));
