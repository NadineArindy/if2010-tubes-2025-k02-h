package src.Station;

import src.Game.StationType;
import src.Item.Item;
import src.Item.KitchenUtensils;
import src.Item.Plate;
import src.Item.Preparable;
import src.chef.Chef;
import src.chef.Position;

public class AssemblyStation extends Workstation {
    public AssemblyStation(String id, Position position, char symbol, StationType type, int capacity, int processTime){
        super(id, position, symbol, type, capacity, processTime);
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

        //fallback
        super.interact(chef);
    }
    
    @Override
        public void startProcess() {
        // Implementasi spesifik di subclass
    }

    @Override
    public void finishProcess() {
        // Implementasi spesifik di subclass
    }
}
