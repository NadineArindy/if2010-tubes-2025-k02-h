package src.Station;

import src.Game.StationType;
import src.Item.Item;
import src.Item.KitchenUtensils;
import src.chef.Chef;
import src.chef.Position;

public class TrashStation extends Station {
    
    public TrashStation(String id, Position position, char symbol, StationType type) {
        super(id, position, symbol, type);
    }

    @Override
    public void interact(Chef chef) {
        if(chef == null){
            return;}

        Item inHand = chef.getInventory();
        if(inHand == null){
            return;
        }

        discard(inHand, chef);
    }
    
    public void discard(Item item, Chef chef){
        if(item == null || chef == null){
            return;
        }

        if(item instanceof KitchenUtensils){
            KitchenUtensils utensil = (KitchenUtensils) item;
            utensil.getContents().clear();
            return;
        }

        if(chef.getInventory() == item){
            chef.setInventory(null);
        }
    }
}
