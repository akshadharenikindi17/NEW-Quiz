package com.adaptivequiz.service;

import com.adaptivequiz.model.Difficulty;

/**
 * Easy+correct -> Medium, Medium+correct -> Hard, Hard+wrong -> Medium, Easy+wrong -> Easy.
 * (Medium+wrong -> Easy, Hard+correct -> Hard.)
 */
public class AdaptiveStrategy implements DifficultyStrategy {
    @Override
    public Difficulty nextDifficulty(Difficulty current, boolean answeredCorrectly) {
        return answeredCorrectly ? current.harder() : current.easier();
    }
}
