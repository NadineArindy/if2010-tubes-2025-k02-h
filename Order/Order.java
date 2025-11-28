package Order;

import Recipe.Recipe;

public class Order {
    private final int position;
    private final Recipe recipe;
    private final int reward;
    private final int penalty;
    private final int timeLimitSec;
    private final long createdAt;

    public Order(int position, Recipe recipe, int reward, int penalty, int timeLimitSec) {
        this.position = position; this.recipe = recipe; this.reward = reward;
        this.penalty = penalty; this.timeLimitSec = timeLimitSec; this.createdAt = System.currentTimeMillis();
    }

    public int getPosition() { return position; }
    public Recipe getRecipe() { return recipe; }
    public int getReward() { return reward; }
    public int getPenalty() { return penalty; }
    public boolean isExpired() {
        long elapsed = (System.currentTimeMillis() - createdAt) / 1000;
        return elapsed >= timeLimitSec;
    }

    @Override public String toString() {
        return "Order#" + position + " " + recipe.getName() + " (+" + reward + " / " + penalty + ")";
    }
}