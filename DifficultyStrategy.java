package com.adaptivequiz.service;

import com.adaptivequiz.model.Difficulty;

/** Interface: lets us plug in different rules for choosing the next difficulty. */
public interface DifficultyStrategy {
    Difficulty nextDifficulty(Difficulty current, boolean answeredCorrectly);
}
