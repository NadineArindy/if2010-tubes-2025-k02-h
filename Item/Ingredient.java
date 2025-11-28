package Item

import java.util.UUID;

public class Ingredient extends Item {
    public enum IngredientState { RAW, CHOPPED, COOKING, COOKED, BURNED }

    private IngredientState state;

    public Ingredient(String name, IngredientState state) {
        super(UUID.randomUUID().toString(), name);
        if (state == null) throw new IllegalArgumentException("state required");
        this.state = state;
    }

    public IngredientState getState() { 
        return state; 
    }

    public void setState(IngredientState s) { 
        state = s; 
    }
}