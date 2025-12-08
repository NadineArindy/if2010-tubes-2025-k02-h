package src.Station;


public abstract class Station {
    private final String id;
    private final Position position;
    private final char symbol;

    public Station(String id, Position position, char symbol) {
        if(id == null || position == null){
            throw new IllegalArgumentException("id and position cannot be null");
        }
        this.id = id;
        this.position = position;
        this.symbol = symbol;
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

    public abstract void interact(Chef chef);
}
