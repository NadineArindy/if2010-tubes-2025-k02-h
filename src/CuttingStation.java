package src;

import java.util.ArrayList;
import java.util.List;

/**
 * CuttingStation:
 * - Menerima utensil (chopping board) ATAU ingredient chopable
 * - Jika ingredient chopable → proses cutting membutuhkan waktu
 * - Setelah selesai → ingredient berubah menjadi CHOPPED
 * - Bisa ambil/taruh item di atas station
 * - Interact (E) = memproses cutting (durasi)
 * - Pickup/Drop (C) = memindahkan item antara chef & station
 */
public class CuttingStation extends Station {

    private boolean isProcessing = false;
    private int progress = 0;
    private final int requiredTime = 3;  // durasi default chopping

    // Ingredient yang sedang diproses
    private Ingredient currentIngredient = null;

    public CuttingStation(String id, Position position) {
        super(id, position);
    }

    // ============================================================
    //                    PICKUP / DROP (C)
    // ============================================================

    @Override
    public void handlePickupDrop(Chef chef) {
        Item inHand = chef.getInventory();
        Item onTop = this.itemOnTop;

        // ---------------------------------------------
        // 1. Chef tangan kosong
        // ---------------------------------------------
        if (inHand == null) {

            // a. Ambil item yang sudah selesai diproses
            if (onTop != null) {
                chef.setInventory(onTop);
                itemOnTop = null;
                return;
            }

            // b. Ambil ingredient yang sedang tidak diproses
            if (currentIngredient != null && !isProcessing) {
                chef.setInventory(currentIngredient);
                currentIngredient = null;
                progress = 0;
                return;
            }

            return;
        }

        // ---------------------------------------------
        // 2. Chef membawa sesuatu
        // ---------------------------------------------

        // CASE A — Chef membawa cooking utensils (bowl/pan) → tidak valid di cutting
        if (inHand instanceof KitchenUtensils) {
            return;
        }

        // CASE B — Chef membawa plate → deposit hasil potong
        if (inHand instanceof Plate plate && onTop instanceof Ingredient ingDone) {
            if (ingDone.getState() == IngredientState.CHOPPED) {
                try {
                    plate.addIngredient(ingDone);
                    itemOnTop = null;
                } catch (Exception ignored) {}
            }
            return;
        }

        // CASE C — Chef membawa ingredient CHOPABLE
        if (inHand instanceof Ingredient ing) {

            // Tidak boleh menaruh di atas item lain
            if (onTop != null || currentIngredient != null) return;

            // ingredient harus chopable
            if (!(ing instanceof Chopable)) return;

            // Taruh di station untuk dipotong
            currentIngredient = ing;
            chef.setInventory(null);
            return;
        }

        // selain itu → ignore
    }

    // ============================================================
    //                     INTERACT (E)
    // ============================================================

    @Override
    public void interact(Chef chef) {
        // Tidak ada ingredient
        if (currentIngredient == null) return;

        // Ingredient harus chopable
        if (!(currentIngredient instanceof Chopable)) return;

        // Jika sudah diproses sebelumnya
        if (currentIngredient.getState() == IngredientState.CHOPPED) return;

        isProcessing = true;
        progress++;

        // Jika selesai
        if (progress >= requiredTime) {
            currentIngredient.chop();   // ubah state ke CHOPPED
            itemOnTop = currentIngredient;  // letakkan hasil di station
            currentIngredient = null;
            isProcessing = false;
            progress = 0;
        }
    }

    // ============================================================
    //              STATUS (optional untuk debugging)
    // ============================================================

    public boolean isProcessing() {
        return isProcessing;
    }

    public int getProgress() {
        return progress;
    }
}

