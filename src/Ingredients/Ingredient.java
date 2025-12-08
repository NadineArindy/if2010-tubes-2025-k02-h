package src.Ingredients;

import src.Item.Item;
import src.Item.Preparable;

public abstract class Ingredient extends Item implements Preparable {
    IngredientState state;

    public Ingredient(String name) {
        super(name);
        this.state = IngredientState.RAW;
    }

    @Override
    public IngredientState getState(){
        return state;
    }

    @Override
    public void setState(IngredientState state){
        this.state = state;
    }

    @Override
    public boolean isReady(){
        return state == IngredientState.COOKED;
    }
}
