package src;

import h.Exception.ItemException;
import h.Station.Station;
import src.Item;

/**
 * Chef adalah entity utama yang dikendalikan pemain.
 * Chef memiliki posisi, direction, inventory, dan busy state
 * ketika melakukan aksi durasi (cutting, washing, cooking start).
 */
public class Chef {

    private final String id;
    private final String name;

    private Position position;
    private Direction direction = Direction.DOWN;

    // Item yang sedang dipegang chef
    private Item inventory;

    // Busy state: chef tidak bisa bergerak saat memulai aksi durasi
    private boolean busy = false;

    // Jika chef sedang melakukan aksi di station tertentu
    private Station currentWorkingStation = null;

    public Chef(String id, String name, Position position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    // ============================
    //          GETTER
    // ============================

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public Item getInventory() {
        return inventory;
    }

    public boolean isBusy() {
        return busy;
    }

    public Station getCurrentWorkingStation() {
        return currentWorkingWorkingStation;
    }

    // ============================
    //        SETTER / STATE
    // ============================

    public void setInventory(Item item) {
        this.inventory = item;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;

        // Jika busy false → chef selesai bekerja
        if (!busy) {
            this.currentWorkingStation = null;
        }
    }

    public void assignWorkingStation(Station station) {
        this.currentWorkingStation = station;
    }

    // ============================
    //         MOVEMENT
    // ============================

    public void move(Direction dir, GameMap map) {
        // kalau chef sedang busy → TIDAK BISA GERAK
        if (busy) return;

        this.direction = dir;

        Position newPos = position.translate(dir.dx, dir.dy);

        if (map.isWalkable(newPos)) {
            this.position = newPos;
        }
    }

    // ============================
    //       PICKUP / DROP (C)
    // ============================

    /**
     * Mekanik pickup/drop sesuai tombol C.
     * Jika tile di depan station/item valid → lakukan aksi.
     */
    public void pickupOrDrop(GameMap map) {
        Position front = position.translate(direction.dx, direction.dy);
        Station target = map.getStationAt(front);

        if (target != null) {
            target.handlePickupDrop(this);
            return;
        }

        // Kalau tile kosong → drop item ke tanah (jika allowed)
        if (inventory != null) {
            map.dropItem(front, inventory);
            inventory = null;
        }
    }

    // ============================
    //          INTERACT (E)
    // ============================

    /**
     * Interact dengan station sesuai arah chef.
     * handleInteract akan dipanggil oleh masing-masing Station.
     */
    public void interact(GameMap map) {
        Position front = position.translate(direction.dx, direction.dy);
        Station station = map.getStationAt(front);

        if (station == null) return;

        // Interaksi station (cutting, washing, cooking, assembly, etc.)
        try {
            station.interact(this);
        } catch (ItemException e) {
            System.out.println("Interaction failed: " + e.getMessage());
        }
    }

    // ============================
    //      REPRESENTATION
    // ============================

    @Override
    public String toString() {
        return name + " at " + position;
    }
}

