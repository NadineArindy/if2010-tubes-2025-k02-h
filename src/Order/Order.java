package src.Order;

public class Order {

    private final int id;
    private final Recipe recipe;
    private final int reward;
    private final int penalty;
    private final int timeLimitSec;
    private final long createdAt;

    public Order(int id, Recipe recipe, int reward, int penalty, int timeLimitSec) {
        if(recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null");
        }
        if(reward < 0 || penalty < 0) {
            throw new IllegalArgumentException("Reward/Penalty must be non-negative");
        }
        if(timeLimitSec <= 0) {
            throw new IllegalArgumentException("Time limit must be positive");
        }
        this.id = id;
        this.recipe = recipe;
        this.reward = reward;
        this.penalty = penalty;
        this.timeLimitSec = timeLimitSec;
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public int getReward() {
        return reward;
    }

    public int getPenalty() {
        return penalty;
    }

    public boolean isExpired() {
        long elapsed = (System.currentTimeMillis() - createdAt) / 1000;
        return elapsed >= timeLimitSec;
    }

    public int getRemainingTime() { 
        long elapsed = (System.currentTimeMillis() - createdAt) / 1000; 
        return Math.max(0, timeLimitSec - (int) elapsed); 
    }

    @Override
    public String toString() {
        return "Order#" + id + " (" + recipe.getName() + ")";
    }
}