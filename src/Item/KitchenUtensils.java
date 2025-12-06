package src.Item;

import java.util.HashSet;
import java.util.Set;

abstract class KitchenUtensils extends Item{
    Set<Preparable> contents;

    public KitchenUtensils(String name) {
        super(name);
        contents = new HashSet<>();
    }

    public Set<Preparable> getContents(){
        return contents;

    }

    public void ensureCanAdd(Preparable p){
        if (contents.size() >= capacity()){
            throw new UtensilFullException(getName() + " is full");
        }

        if (!canAccept(p)){
            throw new IncompatibleIngredientException("Ingredient " + p.getClass().getSimpleName() + " is not acceptable for " + getName());
        }
    }

    public void addToContents(Preparable p){
        ensureCanAdd(p);
        contents.add(p);
    }

    public abstract boolean isPortable();
    public abstract int capacity();
    public abstract boolean canAccept(Preparable ingredient);
    public abstract void addIngredient(Preparable ingredient);

}
