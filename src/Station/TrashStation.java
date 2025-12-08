package src.Station;

import src.Item.Item;
import src.Item.KitchenUtensils;

public class TrashStation extends Station {
    
    public TrashStation(String id, Position position, char symbol) {
        super(id, position, symbol);
    }

    @Override
    public void interact(Chef chef) {
        if(chef == null){
            return;}

        Item innHand = chef.getInventory();
        if(innHand == null){
            return;
        }

        discard(innHand, chef);
    }
    
    public void discard(Item item, Chef chef){
        if(item == null || chef == null){
            return;
        }

        if(item instanceof KitchenUtensils utensil){
            utensil.getContents().clear();
            return;
        }

        if(chef != null && chef.getInventory() == item){
            chef.setInventory(null);
        }
    }
}
