package order;

import recipe.Recipe;

public class Order {

    private final int id;
    private final Recipe recipe;
    private final int reward;
    private final int penalty;
    private final int timeLimitSec;
    private final long createdAt;

    public Order(int id, Recipe recipe, int reward, int penalty, int timeLimitSec) {
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

    @Override
    public String toString() {
        return "Order#" + id + " (" + recipe.getName() + ")";
    }
}