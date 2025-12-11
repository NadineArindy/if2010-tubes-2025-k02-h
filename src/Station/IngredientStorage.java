package src.Station;

import java.lang.reflect.InvocationTargetException;

import src.Game.GameContext;
import src.Game.StationType;
import src.Item.Item;
import src.Item.KitchenUtensils;
import src.Item.Plate;
import src.Item.Preparable;
import src.chef.Chef;
import src.chef.Position;

public class IngredientStorage extends Station {
    private final Class<? extends Preparable> ingredientClass;
    private final boolean infiniteSupply;
    private Item itemOnTop;

    public IngredientStorage(String id, Position position, char symbol, StationType type, Class<? extends Preparable> ingredientClass, boolean infiniteSupply) {
        super(id, position, symbol, type);
        if(ingredientClass == null){
            throw new IllegalArgumentException("ingredientClass cannot be null");
        }
        this.ingredientClass = ingredientClass;
        this.infiniteSupply = infiniteSupply;
        this.itemOnTop = null;
    }
    
    public Class<? extends Preparable> getIngredientClass() {
        return ingredientClass;
    }

    public boolean isInfiniteSupply() {
        return infiniteSupply;
    }

    public Item getItemOnTop() {
        return itemOnTop;
    }

    // Membuat instance ingredient baru dari ingredientClass
    private Preparable dispenseIngredient(){
        try {
            return ingredientClass.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Failed to create ingredient instance", e);
        }
    }

    @Override
    public void interact(Chef chef) {
        if(chef == null){
            return;
        }

        Item inHand = chef.getInventory();
        Item onTop = itemOnTop; 

        System.out.println("IngredientStorage[" + getId() + "] interact: " +
                            "inHand=" + (inHand == null ? "null" : inHand.getClass().getSimpleName()) +
                            ", onTop=" + (onTop == null ? "null" : onTop.getClass().getSimpleName()));


        //CASE 1: Chef memiliki piring bersih di tangan dan ada item di workstation tapi tidak berada di dalam utensil
        if(inHand instanceof Plate && ((Plate) inHand).isClean() && onTop instanceof Preparable && !(onTop instanceof KitchenUtensils)){
            Plate plateInHand = (Plate) inHand;
            Preparable preparable = (Preparable) onTop;
            try{
                plateInHand.addIngredient(preparable);
                itemOnTop = null;
                itemOnTop = plateInHand;
                chef.setInventory(null);
            
                GameContext.getMessenger().info(
                    "Plating: " + preparable.getClass().getSimpleName() +
                    " diambil dari rak dan diletakkan di atas plate."
                );

            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                    "Gagal plating di IngredientStorage: " + e.getMessage()
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
                    " dipindah ke plate di atas rak."
                );
            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                    "Gagal memindahkan isi " + utensilInHand.getName() +
                    " ke plate di rak: " + e.getMessage()
                );
            }
            return;
        }

        //CASE 4: Chef tangan kosong
        if(inHand == null){
            if(onTop != null){
                chef.setInventory(onTop);
                itemOnTop = null;
            } else {
                Preparable newIngredient = dispenseIngredient();
                chef.setInventory((Item) newIngredient);
                if(!infiniteSupply){
                    itemOnTop = (Item) newIngredient;
                }
            }
            return;
        }

        // CASE 5: Chef pegang item, rak kosong -> taruh item di atas rak
        if(onTop == null){
            itemOnTop = inHand;
            chef.setInventory(null);

            GameContext.getMessenger().info(
                    inHand.getClass().getSimpleName() +
                    " diletakkan di atas IngredientStorage."
            );
            
            return;
        }

        System.out.println("[IngredientStorage " + getId() + "] interact by " + chef.getName());
    }
}
