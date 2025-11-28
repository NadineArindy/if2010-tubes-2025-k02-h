public class Shrimp extends Ingredient implements Chopable, Cookable {

    public Shrimp(String name) {
        super(name);
    }

    @Override
    public boolean isCooked() {
        return state == IngredientState.COOKED;
    }

    @Override
    public void cook() {
        if(state == IngredientState.CHOPPED){
            state = IngredientState.COOKING;
        }else if(state == IngredientState.COOKING){
            state = IngredientState.COOKED;
        } else if(state == IngredientState.COOKED){
            state = IngredientState.BURNED;
        }
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
        return isCooked() && isChopped();
    }
    
}
