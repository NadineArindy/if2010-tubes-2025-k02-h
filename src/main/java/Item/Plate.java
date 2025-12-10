package Item;

public class Plate extends KitchenUtensils{
    public Plate(String name) {
        super(name);
    }

    private boolean clean = true;

    public boolean isClean(){
        return clean;
    }

    public void setClean(boolean clean){
        this.clean = clean;
    }

    @Override
    public boolean isPortable() {
       return true;
    }
    @Override
    public int capacity() {
        return 5;
    }

    @Override
    public boolean canAccept(Preparable ingredient) {
        return ingredient.isReady();
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if(!clean){
            throw new PlateDirtyException("Cannot place ingredient on dirty plate");
        }

        if(contents.size() >= capacity()) {
            throw new UtensilFullException("Plate is full (capacity = " + capacity() + ")");        }

        if (!canAccept(ingredient)){
            throw new IncompatibleIngredientException("Ingredient " + ingredient.getClass()+ " is not ready / incompatible with plate");
        }
        
        contents.add(ingredient);
    }
}
