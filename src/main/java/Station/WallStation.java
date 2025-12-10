package Station;

public class WallStation extends Station {
    public WallStation(String id, Position pos, char symbol) {
        super(id, pos, symbol);
    }

    @Override
    public void interact(Chef chef) {
        // Dinding tidak bisa diinteraksi
    }
}
