package src.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import src.Ingredients.Ingredient;
import src.Item.Item;
import src.Item.KitchenUtensils;
import src.Item.Plate;
import src.Item.Preparable;

// Class Helper untuk menampilkan teks di HUD
public final class HudUtil {

    private HudUtil() {}

    public static String formatHeldItem(Item item) {
        if (item == null) return "nothing";

        // === PLATE ===
        if (item instanceof Plate plate) {
            StringBuilder sb = new StringBuilder();

            String dishName = detectDishName(plate);
            if (dishName != null) {
                sb.append("Dish: ").append(dishName);
                sb.append(" on Plate ");
            } else {
                sb.append("Plate ");
            }

            sb.append(plate.isClean() ? "[clean]" : "[dirty]");

            // Jika di piring ada isinya, tampilkan semua isinya
            if (!plate.getContents().isEmpty()) {
                sb.append(" containing: ");
                sb.append(formatPreparableSet(plate.getContents()));
            }
            return sb.toString();
        }

        // === UTENSILS ===
        if (item instanceof KitchenUtensils ku) {
            StringBuilder sb = new StringBuilder();
            sb.append(ku.getName()).append(" (utensil)");
            // Jika di utensil ada isinya, maka tampilkan isinya
            if (!ku.getContents().isEmpty()) {
                sb.append(" containing: ");
                sb.append(formatPreparableSet(ku.getContents()));
            }
            return sb.toString();
        }

        // === INGREDIENT ===
        if (item instanceof Ingredient ing) {
            return ing.getName() + " (" + ing.getState() + ")";
        }

        // default
        return item.getName();
    }

    private static String formatPreparableSet(Set<Preparable> set) {
        if (set == null || set.isEmpty()) return "-";

        List<String> parts = new ArrayList<>();
        for (Preparable p : set) {
            parts.add(formatPreparable(p));
        }

        return String.join(", ", parts);
    }

    // Menampilkan nama + state dari ingredients
    private static String formatPreparable(Preparable p) {
        if (p instanceof Ingredient ing) {
            return ing.getName() + " (" + ing.getState() + ")";
        }
        return p.getClass().getSimpleName();
    }

    // Mendeteksi nama dish berdasarkan isi piring
    private static String detectDishName(Plate plate) {
        // Mengambil OrderManager dari GameContext
        if (GameContext.getOrderManager() == null) return null;

        // Isi piring: Set<Preparable> -> ubah ke list untuk dikirim ke OrderManager
        List<Preparable> comps = new ArrayList<>(plate.getContents());

        // Helper di OrderManager yang mengembalikan nama resep jika kombinasi cocok
        return GameContext.getOrderManager().findMatchingRecipeName(comps); 
    }
}
