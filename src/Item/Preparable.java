package src.Item;

import src.Ingredients.IngredientState;

public interface Preparable {
    public IngredientState getState();
    public void setState(IngredientState state);
    boolean isReady();
}
