package Recipe;

import Exception.InvalidDataException;
import Item.Ingredient;
import Item.Preparable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Recipe {

    private final String name;
    private final List<Ingredient> requiredIngredients; // name + required state

    public Recipe(String name, List<Ingredient> requiredIngredients) throws InvalidDataException {

        if (name == null || name.trim().isEmpty())
            throw new InvalidDataException("Recipe name cannot be empty");

        if (requiredIngredients == null || requiredIngredients.isEmpty())
            throw new InvalidDataException("Recipe must have at least 1 ingredient");

        this.name = name;
        this.requiredIngredients = new ArrayList<>(requiredIngredients);
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getRequiredIngredients() {
        return Collections.unmodifiableList(requiredIngredients);
    }

    /**
     * Matching rules:
     * - jumlah komponen harus sama
     * - nama ingredient harus sama (case-insensitive)
     * - state ingredient harus sama dengan state yang dibutuhkan resep
     */
    // public boolean matches(List<Preparable> dishComponents) {

    //     if (dishComponents == null)
    //         return false;

    //     if (dishComponents.size() != requiredIngredients.size())
    //         return false;

    //     for (Ingredient req : requiredIngredients) {
    //         boolean found = false;

    //         for (Preparable p : dishComponents) {
    //             if (!(p instanceof Ingredient))
    //                 continue;

    //             Ingredient ing = (Ingredient) p;

    //             boolean sameName = ing.getName().equalsIgnoreCase(req.getName());
    //             boolean sameState = ing.getState() == req.getState();

    //             if (sameName && sameState) {
    //                 found = true;
    //                 break;
    //             }
    //         }

    //         if (!found)
    //             return false;
    //     }

    //     return true;
    // }

    public boolean matches(List<Preparable> dishComponents) {
        if (dishComponents == null || dishComponents.size() != requiredIngredients.size())
            return false;

        // Buat salinan agar bisa kita hapus saat ketemu match
        List<Preparable> tempComponents = new ArrayList<>(dishComponents);

        for (Ingredient req : requiredIngredients) {
            boolean found = false;
            
            // Cari match di list sementara
            for (int i = 0; i < tempComponents.size(); i++) {
                Preparable p = tempComponents.get(i);
                if (p instanceof Ingredient) {
                    Ingredient ing = (Ingredient) p;
                    if (ing.getName().equalsIgnoreCase(req.getName()) && 
                        ing.getState() == req.getState()) {
                        
                        tempComponents.remove(i); // HAPUS agar tidak dipakai lagi
                        found = true;
                        break;
                    }
                }
            }
            if (!found) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Recipe{" + name + "}";
    }
}