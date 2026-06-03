package pl.example.cs2.ranking.service;

import org.springframework.stereotype.Component;

@Component
public class EloCalculator {

    private static final int K_FACTOR = 32;

    /**
     * Calculates new ELO rating after a match.
     *
     * @param playerRating   current rating of the player
     * @param opponentRating average rating of the opposing team
     * @param won            true if the player won
     * @return new ELO rating
     */
    public int calculateNewRating(int playerRating, double opponentRating, boolean won) {
        double expectedScore = expectedScore(playerRating, opponentRating);
        double actualScore = won ? 1.0 : 0.0;
        int newRating = (int) Math.round(playerRating + K_FACTOR * (actualScore - expectedScore));
        return Math.max(0, newRating);
    }

    private double expectedScore(int playerRating, double opponentRating) {
        return 1.0 / (1.0 + Math.pow(10, (opponentRating - playerRating) / 400.0));
    }

    public double averageRating(java.util.List<Integer> ratings) {
        return ratings.stream().mapToInt(Integer::intValue).average().orElse(1000.0);
    }
}
