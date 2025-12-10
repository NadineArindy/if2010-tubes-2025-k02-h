package Station;

import Exception.InvalidDataException;
import Exception.OrderNotFoundException;
import Item.Dish;
import Item.Item;
import Item.Plate;
import Item.Preparable;
import Order.OrderManager;
import java.util.Set;

public class ServingCounter extends Station {
    private OrderManager orderManager;
    private KitchenLoop kitchenLoop;
    private java.util.function.Consumer<Integer> onScoreUpdate;

    public ServingCounter(String id, Position position, char symbol, OrderManager orderManager, KitchenLoop kitchenLoop, java.util.function.Consumer<Integer> onScoreUpdate) {
        super(id, position, symbol);
        this.orderManager = orderManager;
        this.kitchenLoop = kitchenLoop;
        this.onScoreUpdate = onScoreUpdate;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public void setOrderManager(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public KitchenLoop getKitchenLoop() {
        return kitchenLoop;
    }

    public void setKitchenLoop(KitchenLoop kitchenLoop) {
        this.kitchenLoop = kitchenLoop;
    }

    @Override
    public void interact(Chef chef) {   
        if(chef == null || kitchenLoop == null || orderManager == null){
            return;
        }

        Item inHand = chef.getInventory();

        if(!(inHand instanceof Plate plate)){
            return;
        }

        if(!plate.isClean()){
            return;
        }

        Dish dish;

        try{
            dish = builDishFromPlate(plate);
        } catch (InvalidDataException e){
            cleanupPlate(plate);
            chef.setInventory(null);
            kitchenLoop.schedulePlateReturn(plate);
            return;
        }

        try {
            int reward = orderManager.processServedDish(dish);
            if (onScoreUpdate != null) onScoreUpdate.accept(reward);
        } catch (OrderNotFoundException e) {
            // pinalti jika dish tidak sesuai dengan order yang ada
            if (onScoreUpdate != null) onScoreUpdate.accept(-50);
        } catch (InvalidDataException e) {
            
        }

        cleanupPlate(plate);
        chef.setInventory(null);
        kitchenLoop.schedulePlateReturn(plate);
    }

    public Dish builDishFromPlate(Plate plate) throws InvalidDataException{
        Set<Preparable> contents = plate.getContents();
        if(contents == null || contents.isEmpty()){
            throw new InvalidDataException("Plate is empty, cannot build dish");
        }

        Dish dish = new Dish();
        for(Preparable p : contents){
            dish.addComponents(p);
        }

        return dish;
    }

    public void cleanupPlate(Plate plate){
        plate.getContents().clear();
    }
}
