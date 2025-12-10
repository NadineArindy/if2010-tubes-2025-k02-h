package Station;

import Item.*;
import Game.Direction;

public class Chef {
    private String id;
    private String name;
    private Item inventory;
    private Position position;
    private Direction direction;
    private boolean isBusy;

    public Chef(String id, String name, Position position) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.direction = Direction.DOWN; // Default direction
        this.isBusy = false;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public Item getInventory() {
        return inventory;
    }

    public void setInventory(Item inventory) {
        this.inventory = inventory;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Chef{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
    public Position getFacingPosition() {
    int x = position.getX();
    int y = position.getY();

    switch (direction) {
        case UP:    return new Position(x, y - 1);
        case DOWN:  return new Position(x, y + 1);
        case LEFT:  return new Position(x - 1, y);
        case RIGHT: return new Position(x + 1, y);
        default:    return position;
    }
}

}
