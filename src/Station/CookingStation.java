package src.Station;

import src.Game.GameContext;
import src.Game.HudUtil;
import src.Game.MusicPlayer;
import src.Game.StationType;
import src.Ingredients.Cookable;
import src.Ingredients.IngredientState;
import src.Item.Item;
import src.Item.KitchenUtensils;
import src.Item.Plate;
import src.Item.Preparable;
import src.chef.Chef;
import src.chef.Position;

public class CookingStation extends Workstation {
    private KitchenUtensils cookingUtensil;
    private boolean isCooking;
    private int remainingTime;
    private boolean cookedStageTriggered;
    private boolean burnedStageTriggered;
    private KitchenUtensils lastCookingUtensil;
    public static final int COOKING_TIME = 12_000;
    public static final int BURNING_TIME = 24_000;

    private MusicPlayer cookingSfx;

    public CookingStation(String id, Position position, char symbol, StationType type, int capacity, int processTime) {
        super(id, position, symbol, type, capacity, processTime);
        this.cookingUtensil = null;
        this.isCooking = false;
        this.remainingTime = 0;
        this.cookedStageTriggered = false;
        this.burnedStageTriggered = false;
        this.lastCookingUtensil = null;
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

    // Cek apakah utensil ada ingredients yang bisa dimasak
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
            if (p instanceof Cookable) {
                Cookable cookable = (Cookable) p;
                try {
                    cookable.cook();
                } catch (RuntimeException e) {
                    // Jika ada yang tidak valid, abaikan
                }
            }
        }
    }

    private void startCooking(KitchenUtensils utensil){
        this.cookingUtensil = utensil;
        this.isCooking = true;

        // RESET TOTAL SETIAP START COOKING
        this.remainingTime = 0;
        this.cookedStageTriggered = false;
        this.burnedStageTriggered = false;

        // Play SFX
        if (cookingSfx != null) {
            cookingSfx.stop();
        }
        cookingSfx = new MusicPlayer();
        cookingSfx.playLoop("resources/assets/sfx/tick.wav");

        GameContext.getMessenger().info(
                "Cooking: mulai memasak di " + utensil.getName()
        );

        this.remainingTime = 0;
        this.cookedStageTriggered = false;
        this.burnedStageTriggered = false;

        // Ubah state awal menjadi COOKING
        for (Preparable p : utensil.getContents()) {
            if (p instanceof Cookable cookable) {
                IngredientState state = p.getState();

                // Rice: RAW -> COOKING
                // Shrimp: CHOPPED -> COOKING
                if (state == IngredientState.RAW || state == IngredientState.CHOPPED) {
                    try {
                        cookable.cook();
                    } catch (RuntimeException e) {
                        // Jika ada yang tidak valid, abaikan 
                    }
                }
            }
        }
    }

    private void stopCooking(){
        this.isCooking = false;
        this.lastCookingUtensil = this.cookingUtensil;
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
            GameContext.playSfx("resources/assets/sfx/bell.wav", 1);
            if (cookingSfx != null) cookingSfx.stop();
        }

        if(!burnedStageTriggered && remainingTime >= BURNING_TIME){
            advanceCookables(cookingUtensil);
            burnedStageTriggered = true;
            GameContext.playSfx("resources/assets/sfx/burned.wav", 1);
            if (cookingSfx != null) cookingSfx.stop();
        }
    }

    /**
     * Progress untuk bar di atas CookingStation.
     * Mengembalikan:
     * - -1 kalau tidak ada proses masak yang sedang berjalan
     * - 0.0 s/d 1.0 untuk progress menuju COOKING_TIME (bukan sampai gosong)
     */

    @Override
    public float getProgress() {
        if (!isCooking || cookingUtensil == null) {
            return -1f;
        }

        float progress = remainingTime / (float) COOKING_TIME;
        return Math.min(progress, 1f);
    }

    @Override
    public void interact(Chef chef) {
        if(chef == null){
            return;
        }

        Item inHand = chef.getInventory();
        Item onTop = peekTopItem(); 
        
        //CASE 0: Chef pegang ingredient, di meja ada cooking utensil
        if (inHand instanceof Preparable prep && onTop instanceof KitchenUtensils device) {
            KitchenUtensils utensilOnTable = (KitchenUtensils) device;
            try {
                utensilOnTable.addIngredient(prep);
                chef.setInventory(null);

                if (hasCookableContents(utensilOnTable)) {
                    startCooking(utensilOnTable);
                    GameContext.getMessenger().info(
                        "Cooking: " + prep.getClass().getSimpleName() +
                        " dimasukkan ke " + utensilOnTable.getName() +
                        " dan mulai dimasak."
                    );
                } 

            } catch (RuntimeException e) {
                GameContext.getMessenger().error(
                    "Gagal memasukkan " + prep.getClass().getSimpleName()
                    + " ke " + utensilOnTable.getName()
                );    
            }
            return;
        }

        //CASE 1: Chef memiliki piring bersih di tangan dan ada item di workstation tapi tidak berada di dalam utensil
        if (inHand instanceof Plate && ((Plate) inHand).isClean() && onTop instanceof Preparable && !(onTop instanceof KitchenUtensils)) {
            Plate plateInHand = (Plate) inHand;
            Preparable preparable = (Preparable) onTop;
            try{
                plateInHand.addIngredient(preparable);
                removeTopItem();
                addItem(plateInHand);
                chef.setInventory(null);

                GameContext.getMessenger().info(
                    "Plating: " + preparable.getClass().getSimpleName() +
                    " dipindah ke plate di CookingStation."
                );
            } catch (RuntimeException e){}
            return;
        }

        //CASE 2: Chef memiliki piring bersih di tangan dan ingredient di dalam utensil di station
        if (inHand instanceof Plate && ((Plate) inHand).isClean() && onTop instanceof KitchenUtensils) {
            Plate plateInHand2 = (Plate) inHand;
            KitchenUtensils utensilOnTable = (KitchenUtensils) onTop;
            try{
                for(Preparable p : utensilOnTable.getContents()){
                    plateInHand2.addIngredient(p);
                }
                utensilOnTable.getContents().clear();

                GameContext.getMessenger().info(
                        "Plating: ingredients dari " + utensilOnTable.getName() +
                        " dipindah ke plate di tangan chef."
                );
            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                        "Gagal memindahkan isi " + utensilOnTable.getName() +
                        " ke plate: " + e.getMessage()
                );
            }
            return;
        }

        //CASE 3: Ingredient di dalam utensil di tangan chef dan ada piring bersih di station
        if (inHand instanceof KitchenUtensils
                && onTop instanceof Plate
                && ((Plate) onTop).isClean()) {

            KitchenUtensils utensilInHand = (KitchenUtensils) inHand;
            Plate plateOnTable = (Plate) onTop;           try{
                for(Preparable p : utensilInHand.getContents()){
                    plateOnTable.addIngredient(p);
                }
                utensilInHand.getContents().clear();


            GameContext.getMessenger().info(
                    "Plating: ingredients dari " + utensilInHand.getName() +
                    " dipindah ke plate di CookingStation."
            );

            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                        "Gagal memindahkan isi " + utensilInHand.getName() +
                        " ke plate: " + e.getMessage()
                );
            }
            return;
        }

        //CASE 4: Chef pegang utensil dan station punya slot
        if (inHand instanceof KitchenUtensils && !isFull()) {
            KitchenUtensils utensilInHand2 = (KitchenUtensils) inHand;
            if (addItem(utensilInHand2)) {
                chef.setInventory(null);
                if (hasCookableContents(utensilInHand2)) {
                    startCooking(utensilInHand2);
                    GameContext.getMessenger().info(
                            "Cooking: " + utensilInHand2.getName() +
                            " diletakkan di CookingStation dan mulai memasak."
                    );
                } else {
                    GameContext.getMessenger().info(
                            utensilInHand2.getName() +
                            " diletakkan di CookingStation."
                    );
                }
            } else {
                GameContext.getMessenger().error(
                        "CookingStation penuh, tidak bisa meletakkan " + utensilInHand2.getName() + "."
                );
            }
            return;
        }

        //CASE 5: Chef tangan kosong, di station ada utensil
        if (inHand == null && onTop instanceof KitchenUtensils) {
            KitchenUtensils utensilOnTable2 = (KitchenUtensils) onTop;
            Item taken = removeTopItem();
            chef.setInventory(taken);

            String message = HudUtil.formatHeldItem(utensilOnTable2);

            if (utensilOnTable2 == cookingUtensil) {
                stopCooking();
                
                if (burnedStageTriggered) {
                    GameContext.getMessenger().info(
                        "Cooking: ingredients BURNED!, " + message 
                    );
                } else if (cookedStageTriggered && !burnedStageTriggered) {
                    GameContext.getMessenger().info(
                        "Cooking: proses masak selesai, " + message  
                    );
                } else {
                    GameContext.getMessenger().info(
                        "Cooking: proses masak dihentikan, " + message 
                    );
                }
            } else {
                GameContext.getMessenger().info(
                        utensilOnTable2.getName() +
                        " diambil dari CookingStation."
                );
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