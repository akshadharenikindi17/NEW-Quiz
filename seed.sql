-- Sample data loaded automatically the first time the app starts.
INSERT INTO subject(subject_name) VALUES ('Mathematics');
INSERT INTO subject(subject_name) VALUES ('English');
INSERT INTO subject(subject_name) VALUES ('Science');

-- ---------- Mathematics (subject_id = 1) ----------
INSERT INTO question(subject_id,question_text,option_a,option_b,option_c,option_d,correct_option,difficulty) VALUES
(1,'What is 2 + 3?','4','5','6','7','B','EASY'),
(1,'What is 7 - 4?','2','3','4','5','B','EASY'),
(1,'How many sides does a triangle have?','2','3','4','5','B','EASY'),
(1,'What is 12 x 5?','50','55','60','65','C','MEDIUM'),
(1,'What is 144 divided by 12?','10','11','12','14','C','MEDIUM'),
(1,'What is the perimeter of a square with side 6 cm?','12 cm','18 cm','24 cm','36 cm','C','MEDIUM'),
(1,'What is 15% of 200?','20','25','30','35','C','HARD'),
(1,'What is the value of 3 squared plus 4 squared?','12','25','49','7','B','HARD'),
(1,'What comes next: 2, 6, 18, 54, ?','108','126','162','216','C','HARD');

-- ---------- English (subject_id = 2) ----------
INSERT INTO question(subject_id,question_text,option_a,option_b,option_c,option_d,correct_option,difficulty) VALUES
(2,'What is the plural of cat?','cats','cates','cat','catz','A','EASY'),
(2,'What is the opposite of hot?','warm','cold','big','red','B','EASY'),
(2,'Which word is a noun?','run','quickly','apple','blue','C','EASY'),
(2,'What is the past tense of go?','goed','gone','went','going','C','MEDIUM'),
(2,'Which word means the same as happy?','sad','joyful','angry','tired','B','MEDIUM'),
(2,'Choose the correct word: She ___ to school every day.','go','going','goes','gone','C','MEDIUM'),
(2,'What is the opposite of ancient?','old','modern','broken','huge','B','HARD'),
(2,'What is the plural of analysis?','analysises','analysis','analyses','analysi','C','HARD'),
(2,'Which word means the same as abundant?','rare','plentiful','empty','small','B','HARD');

-- ---------- Science (subject_id = 3) ----------
INSERT INTO question(subject_id,question_text,option_a,option_b,option_c,option_d,correct_option,difficulty) VALUES
(3,'On which planet do we live?','Mars','Venus','Earth','Jupiter','C','EASY'),
(3,'In which direction does the Sun rise?','West','North','South','East','D','EASY'),
(3,'Water becomes what when it freezes?','Steam','Ice','Smoke','Oil','B','EASY'),
(3,'Which gas do humans need to breathe?','Oxygen','Hydrogen','Helium','Nitrogen','A','MEDIUM'),
(3,'What process do plants use to make their food?','Digestion','Evaporation','Photosynthesis','Respiration','C','MEDIUM'),
(3,'How many legs does an insect have?','4','6','8','10','B','MEDIUM'),
(3,'Which part of the cell is called its powerhouse?','Nucleus','Ribosome','Mitochondria','Membrane','C','HARD'),
(3,'What is the SI unit of force?','Joule','Watt','Pascal','Newton','D','HARD'),
(3,'Which blood cells help fight infection?','Red blood cells','White blood cells','Platelets','Plasma','B','HARD');
