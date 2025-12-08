package src.Station;

import src.Exception.InvalidDataException;
import src.Exception.OrderNotFoundException;
import src.Item.Dish;
import src.Item.Item;
import src.Item.Plate;
import src.Item.Preparable;
import src.Order.OrderManager;
import java.util.Set;

public class ServingCounter extends Station {
    private OrderManager orderManager;
    private KitchenLoop kitchenLoop;

    public ServingCounter(String id, Position position, char symbol, OrderManager orderManager, KitchenLoop kitchenLoop) {
        super(id, position, symbol);
        this.orderManager = orderManager;
        this.kitchenLoop = kitchenLoop;
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
            // TODO: sambung ke ScoreManager?
        } catch (OrderNotFoundException e) {
            // pinalti jika dish tidak sesuai dengan order yang ada
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
