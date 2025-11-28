import java.util.Set;

abstract class KitchenUtensils {
    Set<Preparable> contents;

    public Set<Preparable> getContents(){
        return contents;

    }

    public void addToContents(Preparable p){
        contents.add(p);
    }

    public abstract boolean isPortable();
    public abstract int capacity();
    public abstract boolean canAccept(Preparable ingredient);
    public abstract void addIngredient(Preparable ingredient);

}
