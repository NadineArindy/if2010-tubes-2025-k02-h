package src.Item;

public interface CookingDevice {
    public void startCooking();
    public void update(double time);
    boolean isPortable();
    int capacity();
    boolean canAccept(Preparable ingredient);
    void addIngredient(Preparable ingredient);
}