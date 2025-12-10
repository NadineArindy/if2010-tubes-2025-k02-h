package Station;

import Item.Item;
import Item.KitchenUtensils;
import Item.Preparable;
import Item.Cookable;
import java.util.List;
import java.util.ArrayList;

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

        // CASE 0: Direct Retrieval of Cooked Food (New Logic)
        // If Chef hand is empty, and there is a Utensil on the stove with Ready (Cooked) content, pick up the content.
        if (inHand == null && onTop instanceof KitchenUtensils utensilOnTable && !utensilOnTable.getContents().isEmpty()) {
            // Check if contents are ready/cooked. Just check the first one or all?
            // Usually, we scoop out the result.
            boolean hasReadyFood = false;
            for(Preparable p : utensilOnTable.getContents()){
                if (p.isReady()) {
                    hasReadyFood = true;
                    break;
                }
            }

            if (hasReadyFood) {
                // Take the food (assuming capacity 1 for now, or take first valid item)
                // Since Rice/Shrimp logic is 1 item per utensil usually?
                // Pot capacity is 1.
                List<Preparable> contents = new ArrayList<>(utensilOnTable.getContents());
                 if (!contents.isEmpty()) {
                    Preparable food = contents.get(0);
                    if (food instanceof Item) {
                        chef.setInventory((Item) food);
                        utensilOnTable.getContents().remove(food);
                        // If empty, stop cooking logic if it was running?
                        if (utensilOnTable.getContents().isEmpty() && isCooking && cookingUtensil == utensilOnTable) {
                            stopCooking();
                        }
                        return;
                    }
                }
            }
        }


        // CASE 4 (Cooking Specific): Ingredient in Hand -> Add to Utensil on Station
        if (inHand instanceof Preparable preparable && onTop instanceof KitchenUtensils utensilOnTable) {
            try {
                // Coba masukkan ingredient ke dalam utensil
                utensilOnTable.addIngredient(preparable);
                // Jika berhasil, kosongkan tangan chef
                chef.setInventory(null);
                
                // Cek apakah perlu mulai masak (misal baru aja masukin nasi)
                if (hasCookableContents(utensilOnTable) && !isCooking) {
                    startCooking(utensilOnTable);
                }
            } catch (RuntimeException e) {
                // Ignore incompatibility/full
            }
            return;
        }

        // CASE 5: Place Utensil (Pot/Pan) onto Station
        if (inHand instanceof KitchenUtensils utensilInHand2 && !isFull()) {
            if (addItem(utensilInHand2)) {
                chef.setInventory(null);
                if (hasCookableContents(utensilInHand2)) {
                    startCooking(utensilInHand2);
                }
            }
            return;
        }

        // Picking up the Utensil itself (handled by Workstation.interact default if Hand is Empty)
        // But we added Case 0 which intercepts picking up FOOD.
        // If Food is NOT ready, Case 0 fails, so it falls through to here.
        // Or if Utensil is empty.
        
        // However, we must ensure if we pick up the Utensil, we stop cooking.
        // Workstation default interact doesn't know about 'stopCooking'.
        // So we might need to intercept the pickup here.
        
        if (inHand == null && onTop instanceof KitchenUtensils utensilOnTable2) {
             // If we reached here, Case 0 didn't trigger (Food not ready or empty).
             // So picking up the Pot is valid.
             Item taken = removeTopItem();
             chef.setInventory(taken);

             if (utensilOnTable2 == cookingUtensil) {
                 stopCooking();
             }
             return;
        }

        // Fallback to Workstation (Handles Plating Cases 1-3)
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