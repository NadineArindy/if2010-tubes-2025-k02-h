package src.Item;

import java.util.List;
import java.util.ArrayList;

public class Dish extends Item{
    private List<Preparable> components;

    public Dish(String name){
        super(name);
        components = new ArrayList<>();
    }

    public Dish() {
        this("Dish"); 
    }

    public void addComponents(Preparable ingredient){
        if (!ingredient.isReady()){
            throw new IncompatibleIngredientException("Ingredient is not in final state, cannot be added to dish");
        }

        components.add(ingredient);
    }

    public List<Preparable> getComponent(){
        return components;
    }
}
