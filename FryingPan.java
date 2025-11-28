public class FryingPan extends KitchenUtensils implements CookingDevice{
    private boolean isCooking = false;
    private double cookTime = 0.0;      // dalam detik atau "tick"
    private static final double COOK_DONE_TIME = 12.0;
    private static final double BURN_TIME      = 24.0;

    @Override
    public boolean isPortable() {
        return true;
    }

    @Override
    public int capacity() {
        return 1;
    }

    @Override
    public boolean canAccept(Preparable ingredient) {
       return ingredient instanceof Shrimp && ingredient.getState() == IngredientState.CHOPPED;
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if (contents.size() < capacity() && canAccept(ingredient)){
            contents.add(ingredient);
        }
    }

    @Override
    public void startCooking() {
        if(contents.isEmpty()) return;

        //cek ingredients dalam content dan ubah state pake c.cook
        for (Preparable p : contents){
            if(p instanceof Cookable){ //yg bisa masuk cuman yg bisa dimasak
                Cookable c = (Cookable) p;
                c.cook();
            }
        }
    }

    public void update(double time){
        if (!isCooking) return;

        cookTime += time;
        //set waktu setelah 12 detik state dari cooking jadi cooked
        if (cookTime >= COOK_DONE_TIME && cookTime < BURN_TIME){
            for (Preparable p : contents){
                if(p instanceof Cookable && p.getState() == IngredientState.COOKING){
                    Cookable c = (Cookable) p;
                    c.cook();
                }
            }
        }

        if (cookTime >= BURN_TIME){
            for (Preparable p : contents){
                if (p instanceof Cookable && p.getState() == IngredientState.COOKED){
                    Cookable c = (Cookable) p;
                    c.cook();
                }
            }
            isCooking = false;
        }
    }
}
