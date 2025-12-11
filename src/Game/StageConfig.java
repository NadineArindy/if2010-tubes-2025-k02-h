package src.Game;

import java.util.List;
import src.Order.Recipe;

public class StageConfig {
    public final String name;
    public final int durationSeconds;
    public final int targetScore;
    public final int maxFailedOrders;
    public final int maxConcurrentOrders;
    public final List<Recipe> allowedRecipes;

    public StageConfig(String name, int durationSeconds, int targetScore, int maxFailedOrders, int maxConcurrentOrders, List<Recipe> allowedRecipes) {
        this.name = name;
        this.durationSeconds = durationSeconds;
        this.targetScore = targetScore;
        this.maxFailedOrders = maxFailedOrders;
        this.maxConcurrentOrders = maxConcurrentOrders;
        this.allowedRecipes = allowedRecipes;
    }

    public String getName() {
        return name;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getTargetScore() {
        return targetScore;
    }
}
