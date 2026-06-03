package pl.example.cs2.ranking;

import org.junit.jupiter.api.Test;
import pl.example.cs2.ranking.service.EloCalculator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EloCalculatorTest {

    private final EloCalculator calc = new EloCalculator();

    @Test
    void winnerRatingShouldIncreaseAfterBeatingWeakerOpponent() {
        int newRating = calc.calculateNewRating(1000, 800, true);
        assertTrue(newRating > 1000, "Winner should gain ELO when beating weaker opponent");
    }

    @Test
    void loserRatingShouldDecreaseAfterLosingToStrongerOpponent() {
        int newRating = calc.calculateNewRating(1000, 1200, false);
        assertTrue(newRating < 1000, "Loser should lose ELO when losing to stronger opponent");
    }

    @Test
    void winnerGainsLessEloWhenBeatingMuchWeakerOpponent() {
        int gainVsWeak = calc.calculateNewRating(1500, 800, true) - 1500;
        int gainVsEqual = calc.calculateNewRating(1000, 1000, true) - 1000;
        assertTrue(gainVsWeak < gainVsEqual, "Gain should be smaller when beating much weaker opponent");
    }

    @Test
    void ratingCannotGoBelowZero() {
        int newRating = calc.calculateNewRating(5, 3000, false);
        assertTrue(newRating >= 0, "Rating cannot be negative");
    }

    @Test
    void averageRatingCalculatedCorrectly() {
        double avg = calc.averageRating(List.of(1000, 1200, 800));
        assertEquals(1000.0, avg, 0.01);
    }
}
