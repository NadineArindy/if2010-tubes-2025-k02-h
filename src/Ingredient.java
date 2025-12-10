package src;


public abstract class Ingredient extends Item implements Preparable {
    IngredientState state;

    public Ingredient(String name) {
        super(name);
        this.state = IngredientState.RAW;
    }

    public IngredientState getState(){
        return state;
    }

    public void setState(IngredientState state){
        this.state = state;
    }

    @Override
    public boolean isReady(){
        return state==IngredientState.COOKED;
    }

}
