package src.Station;

import src.Item.Item;
import src.Item.KitchenUtensils;
import src.Item.Plate;
import src.Item.Preparable;
import src.Item.Cookable;

public class CookingStation extends Workstation {
    private KitchenUtensils cookingUtensil;
    private boolean isCooking;
    private int remainingTime;
    private boolean cookedStageTriggered;
    private boolean burnedStageTriggered;
    public static final int COOKING_TIME = 12_000;
    public static final int BURNING_TIME = 24_000;

    public CookingStation(String id, Position position, char symbol, int capacity, int processTime) {
        super(id, position, symbol, capacity, processTime);
        this.cookingUtensil = null;
        this.isCooking = false;
        this.remainingTime = 0;
        this.cookedStageTriggered = false;
        this.burnedStageTriggered = false;
    }

    public boolean isCooking() {
        return isCooking;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public KitchenUtensils getCookingUtensil() {
        return cookingUtensil;
    }

    private boolean hasCookableContents(KitchenUtensils utensil){
        for(Preparable p : utensil.getContents()){
            if(p instanceof Cookable){
                return true;
            }
        }
        return false;
    }

    private void advanceCookables(KitchenUtensils utensil){
        for(Preparable p : utensil.getContents()){
            if(p instanceof Cookable cookable){
                try {
                    cookable.cook();
                } catch (RuntimeException e) {
                    
                }
            }
        }
    }

    private void startCooking(KitchenUtensils utensil){
        this.cookingUtensil = utensil;
        this.remainingTime = 0;
        this.isCooking = true;
        this.cookedStageTriggered = false;
        this.burnedStageTriggered = false;

        advanceCookables(utensil);
    }

    private void stopCooking(){
        this.isCooking = false;
        this.cookingUtensil = null;
    }

    public void update(int deltaTime){
        if(!isCooking || cookingUtensil == null){
            return;
        }

        remainingTime += deltaTime;

        if(!cookedStageTriggered && remainingTime >= COOKING_TIME){
            advanceCookables(cookingUtensil);
            cookedStageTriggered = true;
        }

        if(!burnedStageTriggered && remainingTime >= BURNING_TIME){
            advanceCookables(cookingUtensil);
            burnedStageTriggered = true;
        }
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

        if (inHand instanceof KitchenUtensils utensilInHand2 && !isFull()) {
            if (addItem(utensilInHand2)) {
                chef.setInventory(null);
                if (hasCookableContents(utensilInHand2)) {
                    startCooking(utensilInHand2);
                }
            }
            return;
        }

        if (inHand == null && onTop instanceof KitchenUtensils utensilOnTable2) {
            Item taken = removeTopItem();
            chef.setInventory(taken);

            if (utensilOnTable2 == cookingUtensil) {
                stopCooking();
            }
            return;
        }

        //fallback
        super.interact(chef);
    }

    @Override
    public void startProcess() {
        startCooking(cookingUtensil);
    }

    @Override
    public void finishProcess() {
        stopCooking();
    }
}