package Station;

import Item.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public abstract class Workstation extends Station {
    private final int capacity;             
    private final int processTime;          
    private boolean isProcessing;           
    private final List<Item> itemsOnTop;    

    public Workstation(String id, Position position, char symbol, int capacity, int processTime) {
        super(id, position, symbol);
        if(capacity <= 0){
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.processTime = Math.max(processTime, 0);
        this.isProcessing = false;
        this.itemsOnTop = new ArrayList<>();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getProcessTime() {
        return processTime;
    }

    public List<Item> getItemsOnTop() {
        return Collections.unmodifiableList(itemsOnTop);
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public boolean isFull() {
        return itemsOnTop.size() >= capacity;
    }

    public boolean hasItems() {
        return !itemsOnTop.isEmpty();
    }

    public Item peekTopItem() {
        if (itemsOnTop.isEmpty()) {
            return null;
        }
        return itemsOnTop.get(itemsOnTop.size() - 1);
    }

    public Item removeTopItem() {
        if (itemsOnTop.isEmpty()) {
            return null;
        }
        return itemsOnTop.remove(itemsOnTop.size() - 1);
    }

    public boolean canAccept(Item item) {
        return item != null && !isFull();
    }

    public boolean addItem(Item item) {
        if (!canAccept(item)) {
            return false;
        }
        itemsOnTop.add(item);
        return true;
    }

    
    public void startProcess() {
        // Implementasi spesifik di subclass
    }

    public void finishProcess() {
        // Implementasi spesifik di subclass
    }

    @Override
    public void interact(Chef chef) {
        if(chef == null) {
            return;
        }

        Item inHand = chef.getInventory();
        Item onTop = peekTopItem();

        // --- SHARED PLATING LOGIC (MOVED FROM SUBCLASSES) ---

        // CASE 1: Chef has Clean Plate, Station has Ingredient (not inside Utensil)
        if(inHand instanceof Plate plateInHand && plateInHand.isClean() && onTop instanceof Preparable preparable && !(onTop instanceof KitchenUtensils)){
            // Removed try-catch to propagate errors
            plateInHand.addIngredient(preparable);
            removeTopItem(); // Remove the ingredient from station
            addItem(plateInHand); 
            chef.setInventory(null);
            return;
        }

        // CASE 2: Chef has Clean Plate, Station has Utensil (Pot/Pan)
        if(inHand instanceof Plate plateInHand2 && plateInHand2.isClean() && onTop instanceof KitchenUtensils utensilOnTable){
            // Pour from Utensil to Plate
            List<Preparable> toTransfer = new ArrayList<>(utensilOnTable.getContents());
            boolean transferred = false;
            for(Preparable p : toTransfer){
                // We propagate error if the first one fails, or should we try all?
                // If we propagate, the user sees "X is incompatible".
                // This is better than silence.
                plateInHand2.addIngredient(p);
                transferred = true;
            }
            if (transferred) {
                utensilOnTable.getContents().clear(); 
            }
            return;
        }

        // CASE 3: Chef has Utensil (Pot/Pan), Station has Clean Plate
        if(inHand instanceof KitchenUtensils utensilInHand && onTop instanceof Plate plateOnTable && plateOnTable.isClean()){
            List<Preparable> toTransfer = new ArrayList<>(utensilInHand.getContents());
            boolean transferred = false;
            for(Preparable p : toTransfer){
                plateOnTable.addIngredient(p);
                transferred = true;
            }
            if (transferred) {
                 utensilInHand.getContents().clear();
            }
            return;
        }

        // CASE 4: Chef has Ingredient (Preparable), Station has Clean Plate
        if(inHand instanceof Preparable preparable && onTop instanceof Plate plateOnTable && plateOnTable.isClean()){
            plateOnTable.addIngredient(preparable);
            chef.setInventory(null);
            return;
        }

        // --- DEFAULT PICK / PLACE LOGIC ---

        // Jika tangan kosong, ambil item dari workstation
        if(inHand == null && hasItems()) {
            Item taken = removeTopItem();
            chef.setInventory(taken);
            return;
        }

        // Jika tangan ada isinya, letakkan ke workstation jika meja tidak penuh
        if(inHand != null && !isFull()) {
            if(addItem(inHand)){
                chef.setInventory(null);
            }
        }
    }
}    