package src;

import src.Position;
import src.Chef;
import src.Item;
import src.ItemException;

/**
 * Kelas dasar untuk semua Station dalam game.
 * 
 * Station bisa:
 * - Berinteraksi dengan Chef (E)
 * - Menangani pickup/drop (C)
 * - Menyimpan item (opsional)
 * - Menjadi dasar bagi station lain (cutting, washing, cooking, dll)
 */
public abstract class Station {

    protected final String id;
    protected final Position position;

    // Item yang ada di atas station (bisa null)
    protected Item itemOnTop;

    public Station(String id, Position position) {
        this.id = id;
        this.position = position;
        this.itemOnTop = null;
    }

    // ============================
    //           GETTER
    // ============================

    public String getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public Item getItemOnTop() {
        return itemOnTop;
    }

    // ============================
    //      PICKUP / DROP (C)
    // ============================

    /**
     * Default handler untuk pickup/drop.
     * 
     * Bisa di-override oleh station turunan jika punya aturan khusus.
     */
    public void handlePickupDrop(Chef chef) {
        Item inHand = chef.getInventory();

        // CASE 1 — Chef empty hand: pickup itemOnTop
        if (inHand == null) {
            if (itemOnTop != null) {
                chef.setInventory(itemOnTop);
                itemOnTop = null;
            }
            return;
        }

        // CASE 2 — Chef membawa item, station kosong → letakkan item
        if (itemOnTop == null) {
            itemOnTop = inHand;
            chef.setInventory(null);
            return;
        }

        // CASE 3 — Keduanya punya item → default: tidak melakukan apapun
        // Station turunan boleh override logic ini
    }

    // ============================
    //          INTERACT (E)
    // ============================

    /**
     * Method umum untuk interaksi (E).
     * Harus dioverride oleh Station turunan.
     */
    public abstract void interact(Chef chef) throws ItemException;

    // ============================
    //       UPDATE PER FRAME
    // ============================

    /**
     * Dipanggil dari GameLoop untuk update station (progress bar, timers, dll).
     * Turunan bisa override jika perlu.
     */
    public void update(double deltaTime) {
        // default: tidak melakukan apa-apa
    }
}

