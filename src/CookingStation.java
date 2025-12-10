package src;

/**
 * CookingStation:
 *
 * - Menerima utensil cooking (FryingPan / BoilingPot)
 * - Ingredient COOKABLE dimasukkan ke utensil
 * - Interact (E) meningkatkan progress
 * - Ketika selesai, state ingredient berubah menjadi COOKED
 * - Jika dibiarkan E terus → bisa BURNED
 * - Chef dapat ambil/taruh utensil dari station
 * - Plate dapat mengambil ingredient dari utensil
 */
public class CookingStation extends Station {

    private boolean isProcessing = false;
    private final int requiredTime = 4;   // durasi memasak
    private final int burnTime = 6;       // lewat threshold → burned
    private int progress = 0;

    private KitchenUtensils utensilOnTop = null; // wajan/panci yang sedang dipakai

    public CookingStation(String id, Position position) {
        super(id, position);
    }

    // ========================================================================
    //                          PICKUP / DROP (C)
    // ========================================================================

    @Override
    public void handlePickupDrop(Chef chef) {
        Item inHand = chef.getInventory();
        Item onStation = utensilOnTop != null ? utensilOnTop : itemOnTop;

        // --------------------------------------------------------------------
        // 1. Chef tangan kosong
        // --------------------------------------------------------------------
        if (inHand == null) {

            // a) Ambil utensil (wajan/panci)
            if (utensilOnTop != null) {
                chef.setInventory(utensilOnTop);
                utensilOnTop = null;
                resetProcess();
                return;
            }

            // b) Ambil itemOnTop biasa
            if (itemOnTop != null) {
                chef.setInventory(itemOnTop);
                itemOnTop = null;
            }

            return;
        }

        // --------------------------------------------------------------------
        // 2. Chef bawa sesuatu
        // --------------------------------------------------------------------

        // A) Chef membawa utensil (wajan / panci)
        if (inHand instanceof KitchenUtensils utensil) {

            // Tidak boleh jika sudah ada utensil di station
            if (utensilOnTop != null) return;

            utensilOnTop = utensil;
            chef.setInventory(null);
            return;
        }

        // B) Chef membawa ingredient cookable → masukkan ke utensil
        if (inHand instanceof Ingredient ing && ing instanceof Cookable cookIng) {

            if (utensilOnTop != null) {
                try {
                    utensilOnTop.addContent(ing);
                    chef.setInventory(null);
                } catch (Exception ignored) {}
            }

            return;
        }

        // C) Chef membawa plate → ambil semua hasil cook yang sudah COOKED
        if (inHand instanceof Plate plate && utensilOnTop != null) {

            // hanya ingredient cooked yang bisa dipindahkan
            var cookedList = utensilOnTop.getCookedIngredients();

            for (Preparable cooked : cookedList) {
                try {
                    plate.addIngredient(cooked);
                } catch (Exception ignored) {}
            }

            // hapus cooked ingredients dari utensil
            utensilOnTop.removeCookedIngredients();

            return;
        }

        // selain ini → ignore
    }

    // ========================================================================
    //                             INTERACT (E)
    // ========================================================================

    @Override
    public void interact(Chef chef) {

        // Harus ada utensil untuk memasak
        if (utensilOnTop == null) return;

        // Harus ada ingredients di dalam utensil
        if (utensilOnTop.getContents().isEmpty()) return;

        isProcessing = true;
        progress++;

        // ---------------------------
        // PROGRESS LOGIC
        // ---------------------------

        // Phase 1: COOKING
        for (Preparable p : utensilOnTop.getContents()) {
            if (p instanceof Cookable cookable) {
                if (cookable.getState() == IngredientState.RAW) {
                    cookable.startCooking();
                }
            }
        }

        // Phase 2: Finish cooking
        if (progress == requiredTime) {
            for (Preparable p : utensilOnTop.getContents()) {
                if (p instanceof Cookable cookable) {
                    cookable.finishCooking();
                }
            }
        }

        // Phase 3: Burned
        if (progress >= burnTime) {
            for (Preparable p : utensilOnTop.getContents()) {
                if (p instanceof Cookable cookable) {
                    cookable.burn();
                }
            }
        }
    }

    // ========================================================================
    //                             HELPERS
    // ========================================================================

    private void resetProcess() {
        progress = 0;
        isProcessing = false;
    }

    public int getProgress() { return progress; }
    public boolean isProcessing() { return isProcessing; }

}

