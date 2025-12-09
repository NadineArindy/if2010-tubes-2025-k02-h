package src.Ingredients;

import src.Exception.InvalidIngredientStateException;

public class Cucumber extends Ingredient implements Chopable {

    public Cucumber(){
        super("Cucumber");
    }
    
    public Cucumber(String name) {
        super(name);
    }

    @Override
    public boolean isChopped() {
        return state == IngredientState.CHOPPED;
    }

    @Override
    public void chop() {
        if (state == IngredientState.RAW){
            state = IngredientState.CHOPPED;
        } 
        else {
            throw new InvalidIngredientStateException("Cannot chop" + getName() + " in state" + state);
        }
    }

    @Override
    public boolean isReady() {
        return isChopped();
    }
}
