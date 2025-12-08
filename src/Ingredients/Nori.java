package src.Ingredients;

public class Nori extends Ingredient{

    public Nori(String name) {
        super(name);
    }

    @Override
    public boolean isReady() {
        return getState() == IngredientState.RAW;
    }
}
