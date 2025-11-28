package Recipe;

import Item.Ingredient;
import Exception.InvalidDataException;
import java.util.*;

/**
 * Recipe uses Ingredient objects that encode required name + required state.
 */
public class Recipe {
    private final String name;
    private final List<Ingredient> required;

    public Recipe(String name, List<Ingredient> required) throws InvalidDataException {
        if (name == null || name.isBlank()) throw new InvalidDataException("name required");
        if (required == null || required.isEmpty()) throw new InvalidDataException("required cannot empty");
        this.name = name; this.required = new ArrayList<>(required);
    }

    public String getName() { return name; }
    public List<Ingredient> getRequired() { return Collections.unmodifiableList(required); }

    public boolean matches(List<Ingredient> dishComponents) {
        if (dishComponents == null) return false;
        if (dishComponents.size() != required.size()) return false;
        // require each required ingredient matched by name and state
        for (Ingredient req : required) {
            boolean found = false;
            for (Ingredient d : dishComponents) {
                if (d.getName().equalsIgnoreCase(req.getName())
                    && d.getState() == req.getState()) { found = true; break; }
            }
            if (!found) return false;
        }
        return true;
    }
}