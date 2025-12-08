package src.Station;

import src.Game.StationType;
import src.chef.Chef;
import src.chef.Position;
public abstract class Station {
    private final String id;
    private final Position position;
    private final char symbol;
    private final StationType type;

    public Station(String id, Position position, char symbol, StationType type) {
        if(id == null || position == null){
            throw new IllegalArgumentException("id and position cannot be null");
        }
        this.id = id;
        this.position = position;
        this.symbol = symbol;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public char getSymbol() {
        return symbol;
    }

    public StationType getType() {
        return type;
    }

    public abstract void interact(Chef chef);
}
