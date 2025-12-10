package Station;

import Item.Chopable;
import Item.Item;
import Item.Preparable;

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
        
        // --- CUTTING SPECIFIC LOGIC ---
        // Logic to PLACE valid chopable ingredient if empty
        if(inHand instanceof Preparable preparable2 && preparable2 instanceof Chopable && currentIngredient == null && !isCutting){
            currentIngredient = preparable2;
             chef.setInventory(null);
            remainingTime = CUTTING_TIME;
            isCutting = true;
            return;
        }

        // Logic to PICK UP result if done
        if (inHand == null && currentIngredient != null && !isCutting && remainingTime <= 0) {
            chef.setInventory((Item) currentIngredient);
            currentIngredient = null;
            return;
        }

        // Logic to START/RESUME cutting (if just interacting without item?)
        // The previous code had: if(inHand == null && currentIngredient != null && !isCutting && remainingTime > 0)
        if (inHand == null && currentIngredient != null && !isCutting && remainingTime > 0) {
            isCutting = true;
            return;
        }

        // --- FALLBACK TO GENERIC PLATING / PICK / PLACE ---
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
