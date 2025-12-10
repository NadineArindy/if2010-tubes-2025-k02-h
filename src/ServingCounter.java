package src;

/**
 * ServingCounter:
 *
 * - Menerima dish dari Chef
 * - Memanggil OrderManager untuk validasi pesanan
 * - Jika cocok → skor reward
 * - Jika salah → penalty
 * - Plate menjadi dirty setelah serve
 * - Tidak bisa menerima item selain Plate/Dish
 */
public class ServingCounter extends Station {

    private final OrderManager orderManager;

    public ServingCounter(String id, Position position, OrderManager orderManager) {
        super(id, position);
        this.orderManager = orderManager;
    }

    // =========================================================================
    //                      PICKUP / DROP (C)
    // =========================================================================

    @Override
    public void handlePickupDrop(Chef chef) {
        Item inHand = chef.getInventory();

        // Serving counter tidak untuk pickup → hanya drop dish
        // Chef tidak bisa mengambil apa pun dari serving counter
        if (inHand == null) return;

        // --------------------------------------------------------------
        // CASE 1 — Chef membawa Plate/Dish
        // --------------------------------------------------------------

        if (inHand instanceof Plate plate) {

            // Plate kosong → tidak bisa serve
            if (plate.getIngredients().isEmpty()) return;

            // Plate akan diproses via interact (E)
            // Drop = langsung serve
            serveDish(chef, plate);
            return;
        }

        // --------------------------------------------------------------
        // CASE 2 — Item bukan Plate/Dish → tidak bisa serve
        // --------------------------------------------------------------

        return;
    }

    // =========================================================================
    //                            INTERACT (E)
    // =========================================================================

    @Override
    public void interact(Chef chef) {
        Item inHand = chef.getInventory();

        if (!(inHand instanceof Plate plate)) return;
        if (plate.getIngredients().isEmpty()) return;

        serveDish(chef, plate);
    }

    // =========================================================================
    //                             SERVE LOGIC
    // =========================================================================

    private void serveDish(Chef chef, Plate plate) {

        Dish dish = new Dish(plate.getIngredients());

        try {
            int score = orderManager.processServedDish(dish);

            // SUCCESS SERVE
            System.out.println("Dish served successfully! Score +" + score);

            // Plate menjadi dirty setelah serve
            plate.makeDirty();

            // Chef tetap memegang plate yang sekarang dirty
            chef.setInventory(plate);

        } catch (OrderNotFoundException e) {

            // WRONG DISH
            System.out.println("Incorrect dish served! Penalty applied.");

            // tetap buat plate kotor
            plate.makeDirty();
            chef.setInventory(plate);

        } catch (InvalidDataException e) {
            System.out.println("ERROR: Dish data invalid.");
        }
    }
}

