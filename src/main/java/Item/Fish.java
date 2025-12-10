package Item;

public class Fish extends Ingredient implements Chopable{

    public Fish() {
        super("Ikan");
    }

    public Fish(String name) {
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
        return this.state == IngredientState.RAW;
    }

    
}
