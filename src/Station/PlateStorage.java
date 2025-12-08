package src.Station;

import java.util.Stack;

import src.Game.StationType;
import src.Item.Item;
import src.Item.Plate;
import src.chef.Chef;
import src.chef.Position;

public class PlateStorage extends Station{
    private final Stack<Plate> plates;
    private final int maxPlates;

    public PlateStorage(String id, Position position, char symbol, StationType type, int maxPlates) {
        super(id, position, symbol, type);
        if (maxPlates <= 0) {
            throw new IllegalArgumentException("maxPlates must be greater than 0");
        }
        this.maxPlates = maxPlates;
        this.plates = new Stack<>();

        for (int i = 0; i < maxPlates; i++) {
            plates.push(new Plate("Plate" + i)); 
        }
    }

    public boolean isEmpty() {
        return plates.isEmpty();
    }

    public boolean isFull() {
        return plates.size() >= maxPlates;
    }

    public int size() {
        return plates.size();
    }

    public void addPlate(Plate plate) {
        if (plate == null) {
            throw new IllegalArgumentException("Plate cannot be null");
        }

        if (isFull()) {
            throw new IllegalStateException("PlateStorage is full");
        }

        plates.push(plate);
    }

    // Mengembalikan piring teratas tanpa menghapusnya dari tumpukan
    public Plate peekPlate() {
        if (isEmpty()) {
            return null;
        }
        return plates.peek();
    }

    // Mengeluarkan dan mengembalikan piring teratas dari tumpukan
    public Plate popPlate() {
        if (isEmpty()) {
            System.out.println("No plates available in storage");
            return null;
        }
        return plates.pop();
    }

    @Override
    public void interact(Chef chef) {
        if (chef == null) {
            return;
        }

        Item inHand = chef.getInventory();
        if (inHand != null) {
            return; 
        }

        Plate top = popPlate();
        if (top != null) {
            chef.setInventory(top);
        }
    }
}
