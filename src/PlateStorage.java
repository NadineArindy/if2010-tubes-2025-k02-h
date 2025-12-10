package src;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * PlateStorage:
 * - Menyediakan piring bersih (clean plate)
 * - Bisa menerima piring kotor (dirty plate)
 * - Bisa memberi plate ke chef
 * - Bisa menaruh plate dari chef ke storage
 *
 * Tahun ini storage dianggap infinite clean-plate dispenser
 * Kecuali kamu set maxStack sendiri.
 */
public class PlateStorage extends Station {

    private final boolean infiniteSupply;
    private final Deque<Plate> plateStack; // clean plates

    public PlateStorage(String id, Position position, boolean infiniteSupply) {
        super(id, position);
        this.infiniteSupply = infiniteSupply;
        this.plateStack = new ArrayDeque<>();
    }

    // ======================================================
    //        SPAWN CLEAN PLATE (jika infinite supply)
    // ======================================================

    private Plate generateCleanPlate() {
        Plate p = new Plate();
        p.clean(); // pastikan clean
        return p;
    }

    // ======================================================
    //                PICKUP / DROP (C)
    // ======================================================

    @Override
    public void handlePickupDrop(Chef chef) {
        Item inHand = chef.getInventory();
        Item onTop = this.itemOnTop;

        // ----------------------------------------------
        // 1. Chef tidak pegang apa-apa
        // ----------------------------------------------
        if (inHand == null) {

            // CASE 1A — Storage ada plate di atas station
            if (onTop instanceof Plate plateOnTop) {
                chef.setInventory(plateOnTop);
                itemOnTop = null;
                return;
            }

            // CASE 1B — Storage stack masih punya plate bersih
            if (!plateStack.isEmpty()) {
                chef.setInventory(plateStack.pop());
                return;
            }

            // CASE 1C — Infinite supply → spawn plate baru
            if (infiniteSupply) {
                chef.setInventory(generateCleanPlate());
                return;
            }

            // CASE 1D — Nothing to give
            return;
        }

        // ----------------------------------------------
        // 2. Chef membawa sesuatu
        // ----------------------------------------------

        // CASE 2A — Chef membawa piring (kotor atau bersih), station kosong → taruh
        if (inHand instanceof Plate plate && onTop == null) {
            // Kalau dirty plate → return ke WashingStation nanti
            // Storage hanya menaruh CLEAN plate

            if (!plate.isClean()) {
                // dirty plate tidak disimpan di storage
                // Chef tetap memegang plate tersebut
                return;
            }

            // CLEAN plate disimpan dalam stack
            plateStack.push(plate);
            chef.setInventory(null);
            return;
        }

        // CASE 2B — Chef membawa ingredient/utensil/dish → tidak valid
        // PlateStorage hanya menerima clean plate
    }

    // ======================================================
    //                INTERACT (E)
    // ======================================================

    @Override
    public void interact(Chef chef) {
        // Plate storage tidak punya proses durasi
        // Interact = sama dengan pickup/drop
        handlePickupDrop(chef);
    }
}

