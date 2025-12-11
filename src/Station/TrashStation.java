package src.Station;

import src.Game.GameContext;
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
            GameContext.getMessenger().info("Tidak ada yang dibuang, tangan chef kosong.");
            return;
        }

        discard(inHand, chef);
    }
    
    public void discard(Item item, Chef chef){
        if(item == null || chef == null){
            return;
        }

        // Buang item di dalam utensil saja
        if(item instanceof KitchenUtensils){
            KitchenUtensils utensil = (KitchenUtensils) item;
            utensil.getContents().clear();

            GameContext.getMessenger().info(
                "Isi " + utensil.getName() + " dibuang ke tempat sampah, utensil tetap di tangan chef."
            );

            return;
        }

        //Buang item yang ada di tangan chef
        if(chef.getInventory() == item){
            String name = item.getName() != null ? item.getName() : item.getClass().getSimpleName();
            chef.setInventory(null);
            GameContext.getMessenger().info(
                name + " dibuang ke tempat sampah."
            );
        }
    }
}
