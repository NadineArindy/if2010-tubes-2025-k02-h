package src.Station;

import src.Game.GameContext;
import src.Game.StationType;
import src.Item.Item;
import src.Item.KitchenUtensils;
import src.Item.Plate;
import src.Item.Preparable;
import src.chef.Chef;
import src.chef.Position;

public class AssemblyStation extends Workstation {
    private Item placedItem;

    public AssemblyStation(String id, Position position, char symbol, StationType type, int capacity, int processTime){
        super(id, position, symbol, type, capacity, processTime);
    }

    public Item getPlacedItem() { return placedItem; }
    public void setPlacedItem(Item item) { this.placedItem = item; }

    @Override
    public void interact(Chef chef) {
        if(chef == null){
            return;
        }

        Item inHand = chef.getInventory(); // item di tangan chef
        Item onTop = peekTopItem();        // item paling atas di workstation

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
                    " ditaruh di plate pada AssemblyStation."
                );
            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                    "Gagal plating: " + e.getMessage()
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
                    "Plating: Ingredients dari utensil ditaruh di plate pada AssemblyStation."
                );
            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                    "Gagal plating: " + e.getMessage()
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
                    "Plating: Ingredients dari utensil di tangan chef ditaruh di plate pada AssemblyStation."
                );
            } catch (RuntimeException e){
                GameContext.getMessenger().error(
                    "Gagal plating: " + e.getMessage()
                );
            }
            return;
        }

        // CASE 4: Chef pegang ingredient yang sudah ready, di station ada plate bersih
        if (inHand instanceof Preparable && onTop instanceof Plate && ((Plate) onTop).isClean()) {
            Preparable ingredient = (Preparable) inHand;

            // Pastikan ingredient sudah ready
            if (!ingredient.isReady()) {
                GameContext.getMessenger().error(
                    ingredient.getClass().getSimpleName() + " belum ready untuk menjadi komponen dish!"
                );return;
            }

            Plate plateOnTable = (Plate) onTop;
            try {
                plateOnTable.addIngredient(ingredient); // bisa lempar PlateDirtyException / UtensilFull / Incompatible
                chef.setInventory(null);                // tangan chef kosong, ingredient pindah ke plate
                
                GameContext.getMessenger().info(
                    "Plating: " + ingredient.getClass().getSimpleName() +
                    " ditaruh di plate pada AssemblyStation."
                );
            } catch (RuntimeException e) {
                GameContext.getMessenger().error(
                    "Gagal plating: " + e.getMessage()
                );
                // Jika gagal (plate kotor / penuh / ingredient tidak compatible) dianggap gagal sehingga tidak ada perubahan
            }
            return;
        }


        // fallback jika semua case di atas tidak cocok
        super.interact(chef);
    }
    
    @Override
        public void startProcess() {
    }

    @Override
    public void finishProcess() {
    }
}
