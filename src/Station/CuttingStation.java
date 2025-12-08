package src.Station;

import src.Item.Chopable;
import src.Item.Item;
import src.Item.KitchenUtensils;
import src.Item.Plate;
import src.Item.Preparable;

public class CuttingStation extends Workstation {
    private Preparable currentIngredient;
    private boolean isCutting;
    private int remainingTime;
    public static final int CUTTING_TIME = 3000; 

    public CuttingStation(String id, Position position, char symbol, int capacity, int processTime) {
        super(id, position, symbol, capacity, processTime);
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
        if(currentIngredient instanceof Chopable chopable){
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
        if(inHand instanceof Plate plateInHand && plateInHand.isClean() && onTop instanceof Preparable preparable && !(onTop instanceof KitchenUtensils)){
            try{
                plateInHand.addIngredient(preparable);
                removeTopItem();
                addItem(plateInHand);
                chef.setInventory(null);
            } catch (RuntimeException e){}
            return;
        }

        //CASE 2: Chef memiliki piring bersih di tangan dan ingredient di dalam utensil di station
        if(inHand instanceof Plate plateInHand2 && plateInHand2.isClean() && onTop instanceof KitchenUtensils utensilOnTable){
            try{
                for(Preparable p : utensilOnTable.getContents()){
                    plateInHand2.addIngredient(p);
                }
                utensilOnTable.getContents().clear();
            } catch (RuntimeException e){}
            return;
        }

        //CASE 3: Ingredient di dalam utensil di tangan chef dan ada piring bersih di station
        if(inHand instanceof KitchenUtensils utensilInHand && onTop instanceof Plate plateOnTable && plateOnTable.isClean()){
           try{
                for(Preparable p : utensilInHand.getContents()){
                    plateOnTable.addIngredient(p);
                }
                utensilInHand.getContents().clear();
            } catch (RuntimeException e){}
            return;
        }

        if(inHand instanceof Preparable preparable2 && preparable2 instanceof Chopable && currentIngredient == null && !isCutting){
            currentIngredient = preparable2;
             chef.setInventory(null);
            remainingTime = CUTTING_TIME;
            isCutting = true;
            return;
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
