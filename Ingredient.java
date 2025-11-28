abstract class Ingredient extends Item implements Preparable {
    String name;
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
