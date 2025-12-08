package src.Station;

import src.Game.StationType;
import src.Ingredients.Chopable;
import src.Item.Item;
import src.Item.KitchenUtensils;
import src.Item.Plate;
import src.Item.Preparable;
import src.chef.Chef;
import src.chef.Position;

public class CuttingStation extends Workstation {
    private Preparable currentIngredient;
    private boolean isCutting;
    private int remainingTime;
    public static final int CUTTING_TIME = 3000; 

    public CuttingStation(String id, Position position, char symbol, StationType type, int capacity, int processTime) {
        super(id, position, symbol, type, capacity, processTime);
        this.currentIngredient = null;
        this.isCutting = false;
        this.remainingTime = 0;
    }

    public boolean isCutting() {
        return isCutting;
    }   

    public Preparable getCurrentIngredient() {
        return currentIngredient;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void startCutting(){
        if(currentIngredient == null){
            return;
        }

        if(remainingTime <= 0){
            remainingTime = CUTTING_TIME;
        }

        isCutting = true;
    }

    public void pauseCutting(){
        isCutting = false;
    }

    public void update(int deltaTime){
        if(!isCutting || currentIngredient == null){
            return;
        }

        remainingTime -= deltaTime;
        if(remainingTime <= 0){
            finishCutting();
        }
    }

    public void finishCutting(){
        if(currentIngredient == null){
            return;
        }

        //mengubah state ingredient menjadi terpotong
        if(currentIngredient instanceof Chopable){
            Chopable chopable = (Chopable) currentIngredient;
            try{
                chopable.chop();
            } catch (RuntimeException e){
                //jika tidak bisa dipotong, abaikan
            }
        }

        isCutting = false;
        remainingTime = 0;
    }

    @Override
    public void interact(Chef chef) {
        if(chef == null){
            return;
        }

        Item inHand = chef.getInventory();
        Item onTop = peekTopItem(); 

        //CASE 1: Chef memiliki piring bersih di tangan dan ada item di workstation tapi tidak berada di dalam utensil
        if(inHand instanceof Plate && ((Plate) inHand).isClean() && onTop instanceof Preparable && !(onTop instanceof KitchenUtensils)){
            Plate plateInHand = (Plate) inHand;
            Preparable preparable = (Preparable) onTop;
            try{
                plateInHand.addIngredient(preparable);
                removeTopItem();
                addItem(plateInHand);
                chef.setInventory(null);
            } catch (RuntimeException e){}
            return;
        }

        //CASE 2: Chef memiliki piring bersih di tangan dan ingredient di dalam utensil di station
        if(inHand instanceof Plate && ((Plate) inHand).isClean() && onTop instanceof KitchenUtensils){
            Plate plateInHand2 = (Plate) inHand;
            KitchenUtensils utensilOnTable = (KitchenUtensils) onTop;
            try{
                for(Preparable p : utensilOnTable.getContents()){
                    plateInHand2.addIngredient(p);
                }
                utensilOnTable.getContents().clear();
            } catch (RuntimeException e){}
            return;
        }

        //CASE 3: Ingredient di dalam utensil di tangan chef dan ada piring bersih di station
        if(inHand instanceof KitchenUtensils && onTop instanceof Plate && ((Plate) onTop).isClean()){
            KitchenUtensils utensilInHand = (KitchenUtensils) inHand;
            Plate plateOnTable = (Plate) onTop;
            try{
                for(Preparable p : utensilInHand.getContents()){
                    plateOnTable.addIngredient(p);
                }
                utensilInHand.getContents().clear();
            } catch (RuntimeException e){}
            return;
        }

        if(inHand instanceof Preparable && inHand instanceof Chopable && currentIngredient == null && !isCutting){
            Preparable preparable2 = (Preparable) inHand;
            // Cek lagi untuk memastikan cast ke Chopable aman, meskipun sudah dicek di kondisi
            if (preparable2 instanceof Chopable) {
                currentIngredient = preparable2;
                chef.setInventory(null);
                remainingTime = CUTTING_TIME;
                isCutting = true;
                return;
            }
        }

        if (inHand == null && currentIngredient != null && !isCutting && remainingTime <= 0) {
            chef.setInventory((Item) currentIngredient);
            currentIngredient = null;
            return;
        }

        if (inHand == null && currentIngredient != null && !isCutting && remainingTime > 0) {
            isCutting = true;
            return;
        }

        //fallback
        super.interact(chef);
    }

    @Override
    public void startProcess() {
        startCutting();
    }

    @Override
    public void finishProcess() {
        finishCutting();
    }
}
