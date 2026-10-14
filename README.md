# Adaptive Quiz System for Children

Java (Swing) frontend + application logic, SQL database backend (H2, embedded, accessed via JDBC).
Questions get harder when a child answers correctly and easier when they answer wrongly.

## Features
| Module | What it does |
|---|---|
| Students | Add student (ID, name, age, class, village/school), view all, search by ID |
| Questions | Add question (text, 4 options, answer, difficulty, subject), view all, filter by subject / difficulty |
| Take Quiz | Starts at Easy, one question at a time, adaptive difficulty, every answer saved |
| Results | Name, attempted, correct, wrong, score %, performance level; previous attempts of a student |

Difficulty rules: Easy+correct -> Medium, Medium+correct -> Hard, Hard+wrong -> Medium, Easy+wrong -> stays Easy
(Medium+wrong -> Easy, Hard+correct -> stays Hard). A quiz has up to 10 questions, never repeating a question.

Performance: >=80% Excellent, 60-79% Good, 40-59% Needs Practice, <40% Needs Support.

## Project structure
```
pom.xml                          Maven build (produces target/adaptive-quiz.jar)
db/schema.sql                    Database schema  (deliverable)
db/seed.sql                      Sample subjects + 27 questions
src/main/java/com/adaptivequiz/
  Main.java, DemoRunner.java
  model/    Person, Student, Subject, Question, QuizResult, Difficulty, PerformanceLevel
  dao/      StudentDAO, QuestionDAO, QuizDAO         (all SQL lives here)
  service/  QuizEngine, DifficultyStrategy, AdaptiveStrategy
  ui/       MainFrame, StudentPanel, QuestionPanel, QuizPanel, ResultPanel
.github/workflows/build.yml      GitHub Actions: build + headless demo
.devcontainer/devcontainer.json  GitHub Codespaces with a browser desktop
docs/PROJECT_REPORT.md           Short report, OOP + DB explanation
```

---

## Steps to run on GitHub

### Step 1 - Put the code on GitHub
1. Create a new repository on github.com (e.g. `adaptive-quiz-system`), no README/.gitignore.
2. In the unzipped project folder run:
```bash
git init
git add .
git commit -m "Adaptive Quiz System"
git branch -M main
git remote add origin https://github.com/<your-username>/adaptive-quiz-system.git
git push -u origin main
```
(No git installed? On the repo page use **Add file -> Upload files**, drag in the folder contents, including the hidden `.github` and `.devcontainer` folders, and commit.)

### Step 2 - Check the build and console demo (GitHub Actions)
1. Open the repo -> **Actions** tab. The *Build and Demo* workflow starts automatically on every push.
2. Click the run -> **build** job -> expand **Run headless demo**. You will see the student registered, each question with its difficulty, and the final result. Use this as your *sample input/output*.
3. Scroll down to **Artifacts** to download `adaptive-quiz-jar` (the runnable app).

### Step 3 - Run the real window (GitHub Codespaces)
1. Repo -> green **Code** button -> **Codespaces** tab -> **Create codespace on main**.
2. Wait for setup to finish (a few minutes the first time; it builds the project).
3. Open the **Ports** tab, find port **6080** and click the globe icon. A desktop opens in your browser. Password: `vscode`.
4. In the Codespaces terminal:
```bash
java -jar target/adaptive-quiz.jar
```
The quiz window appears inside the browser desktop. Take your **screenshots** here (all 4 tabs, a quiz question, the result pop-up).

### Run on your own computer instead (needs JDK 17+ and Maven)
```bash
mvn package
java -jar target/adaptive-quiz.jar          # opens the window
java -jar target/adaptive-quiz.jar --demo   # console demo
```
The database is stored in `./data/adaptivequiz.mv.db`. Delete the `data` folder to reset everything.

## Typical walkthrough for screenshots
1. **Students** tab: register `S001`, Asha, 9, class 4, Rampur Village; search `S001`.
2. **Questions** tab: add a question; filter by subject Mathematics + difficulty Hard.
3. **Take Quiz** tab: Student ID `S001`, subject Mathematics, Start Quiz, answer questions.
4. Result pop-up appears, then the **Results** tab lists the attempt.

## Notes
- The demo student `DEMO1` is created only by `--demo`.
- H2 is used so the project runs anywhere without installing MySQL. The schema uses standard SQL, so it can be moved to MySQL by changing the JDBC URL/driver in `Db.java` and `pom.xml`.
