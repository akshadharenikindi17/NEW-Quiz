-- ============================================================
-- Adaptive Quiz System for Children : Database Schema
-- (H2 SQL dialect; also valid, with minor type changes, on MySQL)
-- ============================================================

CREATE TABLE IF NOT EXISTS subject (
    subject_id    INT AUTO_INCREMENT PRIMARY KEY,
    subject_name  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS student (
    student_id      VARCHAR(20)  PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    age             INT          NOT NULL CHECK (age BETWEEN 3 AND 25),
    class_name      VARCHAR(30)  NOT NULL,
    village_school  VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS question (
    question_id     INT AUTO_INCREMENT PRIMARY KEY,
    subject_id      INT          NOT NULL,
    question_text   VARCHAR(500) NOT NULL,
    option_a        VARCHAR(200) NOT NULL,
    option_b        VARCHAR(200) NOT NULL,
    option_c        VARCHAR(200) NOT NULL,
    option_d        VARCHAR(200) NOT NULL,
    correct_option  CHAR(1)      NOT NULL CHECK (correct_option IN ('A','B','C','D')),
    difficulty      VARCHAR(6)   NOT NULL CHECK (difficulty IN ('EASY','MEDIUM','HARD')),
    FOREIGN KEY (subject_id) REFERENCES subject(subject_id)
);

CREATE TABLE IF NOT EXISTS quiz_attempt (
    attempt_id   INT AUTO_INCREMENT PRIMARY KEY,
    student_id   VARCHAR(20) NOT NULL,
    subject_id   INT         NOT NULL,
    started_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (subject_id) REFERENCES subject(subject_id)
);

CREATE TABLE IF NOT EXISTS quiz_response (
    response_id       INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id        INT        NOT NULL,
    question_id       INT        NOT NULL,
    selected_option   CHAR(1)    NOT NULL,
    is_correct        BOOLEAN    NOT NULL,
    difficulty_level  VARCHAR(6) NOT NULL,
    FOREIGN KEY (attempt_id)  REFERENCES quiz_attempt(attempt_id),
    FOREIGN KEY (question_id) REFERENCES question(question_id)
);

CREATE TABLE IF NOT EXISTS quiz_result (
    result_id          INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id         INT           NOT NULL UNIQUE,
    total_attempted    INT           NOT NULL,
    correct_answers    INT           NOT NULL,
    wrong_answers      INT           NOT NULL,
    score_percent      DECIMAL(5,2)  NOT NULL,
    performance_level  VARCHAR(20)   NOT NULL,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempt(attempt_id)
);
