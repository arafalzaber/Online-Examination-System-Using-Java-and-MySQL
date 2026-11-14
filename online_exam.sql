CREATE DATABASE IF NOT EXISTS online_exam CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE online_exam;

-- This project schema replaces the earlier single-student prototype schema.
-- Back up any existing online_exam data before running this script.
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS results;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS exams;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS=1;

CREATE TABLE users (
 id INT AUTO_INCREMENT PRIMARY KEY,
 full_name VARCHAR(120) NOT NULL,
 username VARCHAR(60) NOT NULL UNIQUE,
 password VARCHAR(120) NOT NULL,
 role ENUM('ADMIN','TEACHER','STUDENT') NOT NULL,
 active TINYINT(1) NOT NULL DEFAULT 1,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE exams (
 id INT AUTO_INCREMENT PRIMARY KEY,
 title VARCHAR(180) NOT NULL,
 duration_minutes INT NOT NULL DEFAULT 10,
 created_by INT NOT NULL,
 active TINYINT(1) NOT NULL DEFAULT 1,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_exam_teacher FOREIGN KEY(created_by) REFERENCES users(id)
);

CREATE TABLE questions (
 id INT AUTO_INCREMENT PRIMARY KEY,
 exam_id INT NOT NULL,
 question_text VARCHAR(700) NOT NULL,
 option_a VARCHAR(255) NOT NULL,
 option_b VARCHAR(255) NOT NULL,
 option_c VARCHAR(255) NOT NULL,
 option_d VARCHAR(255) NOT NULL,
 correct_option INT NOT NULL,
 CONSTRAINT chk_correct_option CHECK(correct_option BETWEEN 1 AND 4),
 CONSTRAINT fk_question_exam FOREIGN KEY(exam_id) REFERENCES exams(id) ON DELETE CASCADE
);

CREATE TABLE results (
 id INT AUTO_INCREMENT PRIMARY KEY,
 student_id INT NOT NULL,
 exam_id INT NOT NULL,
 score INT NOT NULL,
 total_questions INT NOT NULL,
 percentage DECIMAL(5,2) NOT NULL,
 submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_result_student FOREIGN KEY(student_id) REFERENCES users(id),
 CONSTRAINT fk_result_exam FOREIGN KEY(exam_id) REFERENCES exams(id) ON DELETE CASCADE
);

-- Demo accounts. Change these passwords before any real deployment.
INSERT INTO users(full_name,username,password,role) VALUES
('System Administrator','admin','admin123','ADMIN'),
('Demo Teacher','teacher','teacher123','TEACHER'),
('Demo Student','student','student123','STUDENT');

INSERT INTO exams(title,duration_minutes,created_by) VALUES
('Java Fundamentals Demo Exam',1,2);

INSERT INTO questions(exam_id,question_text,option_a,option_b,option_c,option_d,correct_option) VALUES
(1,'Which keyword is used to inherit a class in Java?','implements','extends','super','this',2),
(1,'Which collection does not allow duplicate elements?','ArrayList','Vector','HashSet','LinkedList',3),
(1,'What is the entry point of a Java application?','start()','run()','main()','init()',3),
(1,'Which package contains Swing components?','java.net','javax.swing','java.io','java.sql',2),
(1,'Which keyword is used to implement an interface?','extends','implements','import','static',2);
