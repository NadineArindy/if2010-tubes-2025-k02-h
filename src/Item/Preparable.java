package src.Item;

public interface Preparable {
    public IngredientState getState();
    public void setState(IngredientState state);
    boolean isReady();
}
