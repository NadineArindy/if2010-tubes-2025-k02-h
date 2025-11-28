public class Plate extends KitchenUtensils{
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
            System.out.println("Plate is dirty, can't serve!"); //pake throw exception apa ga y
            return;
        }

        if(contents.size() < capacity() && canAccept(ingredient)){
            contents.add(ingredient);
        } else {
            System.out.println("Can't place ingredient on plate.");
        }

    }
}
