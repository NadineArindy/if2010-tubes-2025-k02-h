package Item;

public abstract class Item {
    private final String id;
    private final String name;

    public Item(String id, String name) {
        if (id == null || name == null) throw new IllegalArgumentException("id/name required");
        this.id = id; 
        this.name = name;
    }

    public String getId() { 
        return id; 
    }

    public String getName() { 
        return name; 
    }

    @Override 
    public String toString() { 
        return name + "[" + id + "]"; 
    }
}
