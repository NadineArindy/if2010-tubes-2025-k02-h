package src.Game;

import src.Station.Station;
import src.chef.Position;


public class Tile {
    private final Position position;
    private final TileType type;
    private Station station;   // boleh null
    private final boolean walkable;

    public Tile(Position position, TileType type, boolean walkable) {
        this.position = position;
        this.type = type;
        this.walkable = walkable;
    }

    public Position getPosition() { return position; }
    public TileType getType() { return type; }

    public Station getStation() { return station; }
    public void setStation(Station station) {
        if (this.type != TileType.STATION) {
            throw new IllegalStateException("Cannot assign station to non-station tile");
        }
        this.station = station;
    }


    public boolean hasStation() { return station != null; }
    public boolean isWalkable() { return walkable; }
}

