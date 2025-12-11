package src.Ingredients;

public class Nori extends Ingredient{

    public Nori() {
        this("Nori");
    }
    
    public Nori(String name) {
        super(name);
    }

    @Override
    public boolean isReady() {
        return getState() == IngredientState.RAW;
    }

    public String getName() {
        return "Nori";
    }
}
