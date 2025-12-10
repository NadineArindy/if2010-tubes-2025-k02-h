package src;

import java.lang.reflect.InvocationTargetException;

/**
 * IngredientStorage = sumber bahan mentah (atau siap) sesuai spesifikasi tubes.
 * Bisa infinite supply atau finite (sesuai field).
 * 
 * Aturan penting:
 * - Jika chef tangan kosong → spawn ingredient → chef ambil
 * - Jika chef bawa plate → masukkan ingredient ke plate jika valid
 * - Jika ada utensil → masukkan ingredient ke utensil jika valid
 * - Jika station punya itemOnTop → plate/utensil bisa menggabungkan
 * - Tidak boleh menaruh item sembarang ke station ini
 */
public class IngredientStorage extends Station {

    private final Class<? extends Preparable> ingredientClass;
    private final boolean infiniteSupply;

    public IngredientStorage(String id, Position position,
                             Class<? extends Preparable> ingredientClass,
                             boolean infiniteSupply) {
        super(id, position);
        this.ingredientClass = ingredientClass;
        this.infiniteSupply = infiniteSupply;
    }

    // ======================================================
    //                SPAWN INGREDIENT
    // ======================================================

    private Preparable dispenseIngredient() {
        try {
            return ingredientClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Ingredient instantiation failed: " + ingredientClass.getSimpleName());
        }
    }

    // ======================================================
    //                PICKUP / DROP (C)
    // ======================================================

    @Override
    public void handlePickupDrop(Chef chef) {
        Item inHand = chef.getInventory();
        Item onTop = this.itemOnTop;

        // CASE 1 — Chef kosong & storage kosong → ambil ingredient BARU
        if (inHand == null && onTop == null) {
            Preparable ing = dispenseIngredient();
            chef.setInventory((Item) ing);
            return;
        }

        // CASE 2 — Chef kosong & storage punya item
        if (inHand == null && onTop != null) {
            chef.setInventory(onTop);
            itemOnTop = null;
            return;
        }

        // CASE 3 — Chef bawa Plate (bersih / valid), station kosong → add ingredient ke plate
        if (inHand instanceof Plate plate && onTop == null) {
            Preparable ing = dispenseIngredient();
            try {
                plate.addIngredient(ing);
            } catch (Exception ignored) {}
            return;
        }

        // CASE 4 — Chef bawa Plate & storage punya ingredient → combine
        if (inHand instanceof Plate plate2 && onTop instanceof Preparable prepOnTop) {
            try {
                plate2.addIngredient(prepOnTop);
                itemOnTop = null;
            } catch (Exception ignored) {}
            return;
        }

        // CASE 5 — Chef bawa utensil, station kosong → spawn ing ke utensil
        if (inHand instanceof KitchenUtensils utensil && onTop == null) {
            try {
                utensil.addContent(dispenseIngredient());
            } catch (Exception ignored) {}
            return;
        }

        // CASE 6 — Chef bawa utensil, storage punya ingredient → masukin
        if (inHand instanceof KitchenUtensils utensil2 && onTop instanceof Preparable topPrep) {
            try {
                utensil2.addContent(topPrep);
                itemOnTop = null;
            } catch (Exception ignored) {}
            return;
        }

        // CASE default — tidak boleh menaruh selain plate/utensil/ingredient
        // Storage tidak menerima barang random
    }

    // ======================================================
    //                INTERACT (E)
    // ======================================================

    @Override
    public void interact(Chef chef) {
        // Ingredient storage tidak punya aksi durasi
        // Interact = sama dengan pickup/drop behavior
        handlePickupDrop(chef);
    }
}

