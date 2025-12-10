package src;


public class Rice extends Ingredient implements Cookable{

    public Rice(String name) {
        super(name);
    }

    @Override
    public boolean isCooked() {
        return state == IngredientState.COOKED;
    }

    @Override
    public void cook() {
        if(state == IngredientState.RAW){
            state = IngredientState.COOKING;
        } else if(state == IngredientState.COOKING){
            state = IngredientState.COOKED;
        } else if (state == IngredientState.COOKED){
            state = IngredientState.BURNED;
        } else {
            throw new InvalidIngredientStateException("Cannot cook rice in state " + state);
        }
    }

    @Override
    public boolean isReady() {
        return isCooked();
    }

}
