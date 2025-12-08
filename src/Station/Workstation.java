package src.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import src.Exception.WorkstationFullException;
import src.Game.StationType;
import src.Item.*;
import src.chef.Chef;
import src.chef.Position;
public abstract class Workstation extends Station {
    private final int capacity;             
    private final int processTime;          
    private boolean isProcessing;           
    private final List<Item> itemsOnTop;    

    public Workstation(String id, Position position, char symbol, StationType type, int capacity, int processTime) {
        super(id, position, symbol, type);
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
            throw new WorkstationFullException("Workstation " + getId() + " is full (capacity = " + capacity + ")");
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