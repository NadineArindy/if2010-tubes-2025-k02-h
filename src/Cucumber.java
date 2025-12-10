package src;


public class Cucumber extends Ingredient implements Chopable {

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
    }

    @Override
    public boolean isReady() {
        return isChopped();
    }
    
}
