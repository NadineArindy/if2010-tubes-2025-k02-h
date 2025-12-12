package src.chef;

import src.Game.GameContext;
import src.Game.HudUtil;
import src.Game.GameMap;
import src.Game.Tile;
import src.Item.Item;
import src.Station.Station;
import src.Item.Plate;
import src.Item.Preparable;
import src.Item.KitchenUtensils;
import src.Ingredients.Ingredient;

public class Chef {
    private final String id;
    private final String name;
    private Position position;
    private Direction direction;
    private Item inventory;
    private ActionState currentAction = ActionState.IDLE;
    private Thread actionThread;
    private volatile boolean actionCancelled;
    private Station currentStation;

    public Chef(String id, String name, Position startPos) {
        this.id = id;
        this.name = name;
        this.position = startPos;
        this.direction = Direction.DOWN;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Position getPosition() { return position; }
    public Direction getDirection() { return direction; }
    public Item getInventory() { return inventory; }
    public ActionState getCurrentAction() { return currentAction; }

    // Mengatur item yang dipegang chef
    public void setInventory(Item item) {
        this.inventory = item;

        GameContext.getMessenger().info(
            name + " now holds: " + HudUtil.formatHeldItem(item)
        );
    }

    public boolean isBusy() {
        return currentAction != ActionState.IDLE;
    }

    // Memindahkan chef ke arah tertentu jika tidak sedang busy
    public void move(Direction dir, GameMap map, Chef[] others) {
        // Cek apakah chef sedang busy
        if (isBusy()){
            if (currentStation != null) {
                currentStation.onChefLeave(this);  
            }
            cancelCurrentAction();
            stopBusy();
            currentStation = null;
        }

        // Hitung posisi baru berdasarkan arah
        int newX = position.getX() + dir.dx;
        int newY = position.getY() + dir.dy;

        this.direction = dir;

        if (!map.isWalkable(newX, newY)) return;

        // Cek tabrakan dengan chef lain
        if(others != null){
            for (Chef other : others) {
                if(other == null || other == this) continue;
                Position otherPos = other.getPosition();
                if(other != this) {
                    if (otherPos.getX() == newX && otherPos.getY() == newY) {
                        return; 
                    }
                }
            }
        }

        // Update posisi chef
        position.setX(newX);
        position.setY(newY);
    }

    // Interaksi chef dengan station di depannya
    public void interact(GameMap map) {
        if(map == null) return;
        if (isBusy()) { 
            GameContext.getMessenger().error(name + " sedang busy, tidak bisa berinteraksi.");
            return;
        }

        // Lihat tile di depan chef
        Position front = getFrontPosition();
        Tile tile = map.getTileAt(front.getX(), front.getY());
        if (tile == null) {
            GameContext.getMessenger().error(name + " tidak ada tile di depan.");
            return;
        }

        System.out.println(name + " interact at " + front + " tileType=" + tile.getType() + " hasStation=" + tile.hasStation());

        if (tile.hasStation()) {
            Station station = tile.getStation();
            if(station != null){
                this.currentStation = station;
                GameContext.getMessenger().info(name + " berinteraksi dengan " + station.getClass().getSimpleName());
                station.interact(this);
            } else {
                this.currentStation = null;
                GameContext.getMessenger().error(name + " tidak ada station di tile depan.");
            }
        } else {
            // pick up / put down logic
            this.currentStation = null;
            handleFloorInteraction(tile);
        }
    }

    private void handleFloorInteraction(Tile tile) {
        // implementasi pick up / put down item di lantai
        if (tile == null) return;

        Item inHand   = this.getInventory();
        Item onGround = tile.getGroundItem();

        //CASE 1: Chef pegang plate bersih, di depan ada ingredient edible di luar kitchen utensils
        if (inHand instanceof Plate plate
                && plate.isClean()
                && onGround instanceof Preparable prep
                && !(onGround instanceof KitchenUtensils)
                && isEdible(prep)) {

            try {
                plate.addIngredient(prep);

                tile.setGroundItem(plate);
                this.setInventory(null);

                GameContext.getMessenger().info(
                    "Plating: " + HudUtil.formatHeldItem(plate) +
                    " di lokasi ingredient."
                );
            } catch (RuntimeException e) {
                GameContext.getMessenger().error("Gagal plating di lantai: " + e.getMessage());
            }
            return; 
        }

        //CASE 2: Chef tangan kosong, ada item di lantai 
        if (inHand == null && onGround != null) {
            this.setInventory(onGround);
            tile.setGroundItem(null);
            return;
        }

        //CASE 3: Chef tangan isi, lantai kosong
        if (inHand != null && onGround == null) {
            tile.setGroundItem(inHand);
            this.setInventory(null);
            return;
        }

        //CASE 4: Chef tangan isi tapi lantai juga ada item
        GameContext.getMessenger().error(
            name + " tidak bisa meletakkan item di sini (tile sudah terisi)."
        );    
    }

    private Position getFrontPosition() {
        int x = position.getX() + direction.dx;
        int y = position.getY() + direction.dy;
        return new Position(x, y);
    }

    @Override
    public String toString() {
        return "Chef{" + id + ", pos=" + position + ", dir=" + direction + ", item=" + (inventory != null ? inventory.getName() : "null") + ", state=" + currentAction + "}";
    }

    public enum ActionState {
        IDLE, BUSY
    }

    public void startBusy() {
        this.currentAction = ActionState.BUSY;
    }

    public void stopBusy() {
        this.currentAction = ActionState.IDLE;
    }

    public Station getCurrentStation() {
        return currentStation;
    }

    public void setCurrentStation(Station station) {
        this.currentStation = station;
    }

    public boolean hasRunningAction() {
        return actionThread != null && actionThread.isAlive();
    }

    public void startAsyncAction(Runnable body) {
        if (hasRunningAction()) return;

        startBusy();
        actionCancelled = false;

        actionThread = new Thread(() -> {
            try {
                body.run();   // isi aksi berdurasi
            } finally {
                // kalau aksi selesai (atau dihentikan), chef jadi idle
                stopBusy();
                actionThread = null;
            }
        }, "Chef-" + id + "-Action");

        actionThread.start();
    }

    public void cancelCurrentAction() {
        actionCancelled = true;
    }

    public boolean isActionCancelled() {
        return actionCancelled;
    }

    // Hanya ingredient sudah ready yang boleh di-plating
    private boolean isEdible(Preparable p) {
        if (p instanceof Ingredient ing) {
            return ing.isReady(); 
        }
        return false;
    }

}
