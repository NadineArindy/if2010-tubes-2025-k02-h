package src.Item;

import java.util.ArrayList;
import java.util.List;
import src.Exception.IncompatibleIngredientException;

public class Dish extends Item {
    private List<Preparable> components;

    public Dish(String name) {
        super(name);
        this.components = new ArrayList<>();
    }

    public Dish() {
        this("Dish");
    }

    public void addComponent(Preparable ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }
        if (!ingredient.isReady()) {
            throw new IncompatibleIngredientException("Ingredient is not in final state, cannot be added to dish");
        }
        components.add(ingredient);
    }

    public List<Preparable> getComponents() {
        return components;
    }
}
