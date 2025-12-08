package src.Station;

import src.Item.*;

public class Chef {
    private String id;
    private String name;
    private Item inventory;

    public Chef(String id, String name) {
        this.id = id;
        this.name = name;
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
}
