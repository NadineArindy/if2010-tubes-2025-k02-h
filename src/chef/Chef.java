package src.chef;

import src.Game.GameMap;
import src.Game.Tile;
import src.Item.Item;
import src.Station.Station;

public class Chef {
    private final String id;
    private final String name;
    private Position position;
    private Direction direction;
    private Item inventory;
    private ActionState currentAction = ActionState.IDLE;

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

    public void setInventory(Item item) {
        this.inventory = item;
    }

    public boolean isBusy() {
        return currentAction != ActionState.IDLE;
    }

    public void move(Direction dir, GameMap map, Chef[] others) {
        if (isBusy()) return;

        int newX = position.getX() + dir.dx;
        int newY = position.getY() + dir.dy;

        direction = dir;

        if (!map.isWalkable(newX, newY)) return;

        for (Chef other : others) {
            if (other != this && other.getPosition().equals(new Position(newX, newY))) {
                return; // tidak bisa tabrakan dengan chef lain
            }
        }

        position.setX(newX);
        position.setY(newY);
    }

    public void interact(GameMap map) {
        if (isBusy()) return;

        Position front = getFrontPosition();
        Tile tile = map.getTileAt(front.getX(), front.getY());
        if (tile == null) return;

        if (tile.hasStation()) {
            Station station = tile.getStation();
            currentAction = ActionState.BUSY;
            new Thread(() -> {
                station.interact(this);
                currentAction = ActionState.IDLE;
            }).start();
        } else {
            // pick up / put down logic
            handleFloorInteraction(tile);
        }
    }

    private void handleFloorInteraction(Tile tile) {
        // implementasi pick up / put down item di lantai
    }

    private Position getFrontPosition() {
        int x = position.getX() + direction.dx;
        int y = position.getY() + direction.dy;
        return new Position(x, y);
    }

    @Override
    public String toString() {
        return "Chef{" + id + ", pos=" + position + ", dir=" + direction + ", item=" + inventory + "}";
    }

    public enum ActionState {
        IDLE, BUSY
    }
}
