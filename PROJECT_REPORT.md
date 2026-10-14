# Project Report: Adaptive Quiz System for Children

## 1. Introduction
Children in rural or under-resourced areas learn at different speeds. This project is a desktop quiz
application written in Java (Swing frontend, JDBC) with a relational database backend. It adjusts the
difficulty of the next question according to how the child answered the previous one, so each child is
challenged at the right level.

## 2. Objectives
Java quiz app; store students, questions, attempts, responses and results in a database; Easy/Medium/Hard
questions; scoring; adaptive difficulty; result report with performance level.

## 3. Modules
1. **Student Registration** - add, view, search by Student ID (`StudentPanel`, `StudentDAO`).
2. **Question Management** - add, view, search by subject/difficulty (`QuestionPanel`, `QuestionDAO`).
3. **Quiz** - starts Easy, one question at a time, adaptive logic, each response saved (`QuizPanel`, `QuizEngine`, `QuizDAO`).
4. **Result** - totals, score, performance level, previous attempts (`ResultPanel`, `QuizResult`).

## 4. Adaptive logic
| Last question | Answer | Next difficulty |
|---|---|---|
| Easy | Correct | Medium |
| Medium | Correct | Hard |
| Hard | Correct | Hard |
| Hard | Wrong | Medium |
| Medium | Wrong | Easy |
| Easy | Wrong | Easy |

If no unused question exists at the wanted level, the engine uses the closest level. A quiz ends after 10
questions or when the subject has no unused questions left, or when the student presses *End Quiz*.

Score = correct / attempted x 100. Performance: >=80 Excellent, 60-79 Good, 40-59 Needs Practice, <40 Needs Support.

Example: 10 attempted, 7 correct, 3 wrong -> 70.0% -> **Good**.

## 5. OOP concepts used
- **Encapsulation** - model classes (`Student`, `Question`, `QuizResult`) keep fields `private final` and expose getters only. `Question` validates that it has exactly four options and copies the array.
- **Abstraction** - `Person` is an abstract class with an abstract `describe()`; `DifficultyStrategy` is an interface that hides *how* the next difficulty is chosen. The UI talks to `QuizEngine` and never writes SQL.
- **Inheritance** - `Student extends Person` (reuses name and age). All panels extend `JPanel`.
- **Polymorphism** - `AdaptiveStrategy` implements `DifficultyStrategy`; `QuizEngine` can accept any strategy. `describe()` and `toString()` are overridden. Panels are used through the `Refreshable` interface.
- **Enums** - `Difficulty` and `PerformanceLevel` hold their own behaviour (`harder()`, `easier()`, `fromPercent()`).
- **Separation of concerns (layered design)** - `ui` -> `service` -> `dao` -> database.
- **Exception handling** - `SQLException` is caught in the UI and shown as a friendly message; try-with-resources closes connections.

## 6. Database design
Database: H2 (SQL), accessed through JDBC with `PreparedStatement`s. File: `db/schema.sql`.

| Table | Purpose | Key columns |
|---|---|---|
| `subject` | Subjects | PK `subject_id`, unique `subject_name` |
| `student` | Registered students | PK `student_id`, name, age, class_name, village_school |
| `question` | Quiz questions | PK `question_id`, FK `subject_id`, question_text, option_a-d, correct_option, difficulty |
| `quiz_attempt` | One row per quiz taken | PK `attempt_id`, FK `student_id`, FK `subject_id`, started_at |
| `quiz_response` | One row per answered question | PK `response_id`, FK `attempt_id`, FK `question_id`, selected_option, is_correct, difficulty_level |
| `quiz_result` | Final result of an attempt | PK `result_id`, FK `attempt_id` (unique), total_attempted, correct_answers, wrong_answers, score_percent, performance_level |

### Relationships
```
subject 1 ---- * question
subject 1 ---- * quiz_attempt
student 1 ---- * quiz_attempt
quiz_attempt 1 ---- * quiz_response * ---- 1 question
quiz_attempt 1 ---- 1 quiz_result
```
- A subject has many questions; a question belongs to one subject.
- A student can take many attempts; each attempt belongs to one student and one subject.
- An attempt has many responses (one per question shown); each response refers to one question.
- An attempt has exactly one result (enforced by `UNIQUE(attempt_id)`).
- CHECK constraints restrict `difficulty` to EASY/MEDIUM/HARD and `correct_option` to A-D.

## 7. Sample input / output
Input (Quiz tab): Student ID `S001`, Subject `Mathematics`, answers 10 questions with 7 correct.

Output (result pop-up):
```
Student Name       : Asha
Subject            : Mathematics
Questions Attempted: 10
Correct Answers    : 7
Wrong Answers      : 3
Final Score        : 70.0%
Performance Level  : Good
```
Paste the real console output from the GitHub Actions *Run headless demo* step here, and add the screenshots of all screens.

## 8. Conclusion
The system registers students, manages questions, runs an adaptive quiz, stores every response and result in a
database, and shows previous attempts. Possible future work: more subjects, timed questions, teacher login, charts of progress.
