import java.util.List;
import java.util.ArrayList;

public class Dish {
    private List<Preparable> components;

    public Dish(){
        components = new ArrayList<>();
    }

    public void addComponents(Preparable ingredient){
        if (ingredient.isReady()){
            components.add(ingredient);
        }
    }

    public List<Preparable> getComponent(){
        return components;
    }
}
