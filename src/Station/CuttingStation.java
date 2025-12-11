package src.Station;

import src.Game.GameContext;
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
    private Chef workingChef;
    public static final int CUTTING_TIME = 3000; 

    public CuttingStation(String id, Position position, char symbol, StationType type, int capacity, int processTime) {
        super(id, position, symbol, type, capacity, processTime);
        this.currentIngredient = null;
        this.isCutting = false;
        this.remainingTime = 0;
        this.workingChef = null;
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
        if(!isCutting) return;
        isCutting = false;

        if(workingChef != null){
            workingChef.cancelCurrentAction();
            workingChef.stopBusy();
            workingChef.setCurrentStation(null);
        }
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

        // Mengubah state ingredient menjadi terpotong (CHOPPED)
        if(currentIngredient instanceof Chopable){
            Chopable chopable = (Chopable) currentIngredient;
            try{
                chopable.chop();
            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                    "Gagal memotong " + currentIngredient.getClass().getSimpleName() +
                    ": " + e.getMessage()
                );
            }
        }

        isCutting = false;
        remainingTime = 0;

        if(workingChef != null){
            workingChef.stopBusy();
            workingChef.setCurrentStation(null);
            workingChef = null;
        }
    }

    // Jika chef meninggalkan station, proses berhenti sementara
    @Override
    public void onChefLeave(Chef chef) {
        if(chef == workingChef && isCutting){
            pauseCutting();
        }
    }

    @Override
    public float getProgress() {
        // Jika tidak ada piring yang sedang dicuci, tidak ada progress
        if (currentIngredient == null) {
            return -1f;
        }

        if (remainingTime <= 0 || remainingTime > CUTTING_TIME) {
            return -1f;
        }

        // Hitung berapa persen progress mencuci yang sudah selesai (0.0 - 0.1)
        float done = CUTTING_TIME - remainingTime;
        return done / (float) CUTTING_TIME;
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

                GameContext.getMessenger().info(
                    "Plating: " + preparable.getClass().getSimpleName() +
                    " dipindah ke plate di CuttingStation."
                );
            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                    "Gagal plating di CuttingStation: " + e.getMessage()
                );
            }
            return;
        }

        //CASE 2: Chef memiliki piring bersih di tangan dan ingredient di dalam utensil di station
        if(inHand instanceof Plate && ((Plate) inHand).isClean() && onTop instanceof KitchenUtensils){
            Plate plateInHand2 = (Plate) inHand;
            KitchenUtensils utensilOnTable = (KitchenUtensils) onTop;
            try{
                for(Preparable p : utensilOnTable.getContents()){
                    plateInHand2.addIngredient(p);

                GameContext.getMessenger().info(
                    "Plating: ingredients dari " + utensilOnTable.getName() +
                    " dipindah ke plate di tangan chef."
                );
                }
                utensilOnTable.getContents().clear();
            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                    "Gagal memindahkan isi " + utensilOnTable.getName() +
                    " ke plate: " + e.getMessage()
                );
            }
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

                GameContext.getMessenger().info(
                    "Plating: ingredients dari " + utensilInHand.getName() +
                    " dipindah ke plate di CuttingStation."
                );
            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                    "Gagal memindahkan isi " + utensilInHand.getName() +
                    " ke plate: " + e.getMessage()
                );
            }
            return;
        }

        //CASE 4: Chef pegang ingredient chopable, station kosong
        if(inHand instanceof Preparable && inHand instanceof Chopable && currentIngredient == null && !isCutting){
            Preparable preparable2 = (Preparable) inHand;
            if (preparable2 instanceof Chopable) {
                currentIngredient = preparable2;
                chef.setInventory(null);
                remainingTime = CUTTING_TIME;
                isCutting = true;

                startCuttingAsync(chef);

                return;
            }
        }

        //CASE 5: Chef tangan kosong, ingredient sudah selesai dipotong
        if (inHand == null && currentIngredient != null && !isCutting && remainingTime <= 0) {
            chef.setInventory((Item) currentIngredient);
            GameContext.getMessenger().info(
                "Cutting: hasil potongan "
                + currentIngredient.getClass().getSimpleName()
                + " diambil oleh chef."
            );
            currentIngredient = null;
            return;
        }

        //CASE 6: Chef tangan kosong, ada ingredient tapi proses sedang pause
        if (inHand == null && currentIngredient != null && !isCutting && remainingTime > 0) {
            startCuttingAsync(chef);
            
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

    private void startCuttingAsync(Chef chef) {
        if (currentIngredient == null) return;

        if (remainingTime <= 0) {
            remainingTime = CUTTING_TIME; // mulai dari awal
        }

        isCutting = true;
        workingChef = chef;

        chef.setCurrentStation(this);
        chef.startAsyncAction(() -> {
            long last = System.currentTimeMillis();

            // Loop di THREAD TERPISAH
            while (!chef.isActionCancelled() && remainingTime > 0) {
                try {
                    Thread.sleep(50); // step kecil biar progress halus
                } catch (InterruptedException e) {
                    break;
                }

                long now = System.currentTimeMillis();
                int dt = (int) (now - last);
                last = now;

                remainingTime -= dt;
            }

            // Keluar loop → berhenti atau selesai
            isCutting = false;

            if (!chef.isActionCancelled() && remainingTime <= 0) {
                // selesai motong
                finishCutting();
            }
        });
    }

}
