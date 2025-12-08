package src.Station;

import java.util.Set;

import src.Exception.InvalidDataException;
import src.Exception.OrderNotFoundException;
import src.Game.ScoreManager;
import src.Game.StationType;
import src.Item.Dish;
import src.Item.Item;
import src.Item.Plate;
import src.Item.Preparable;
import src.Order.OrderManager;
import src.chef.Chef;
import src.chef.Position;

public class ServingCounter extends Station {
    private OrderManager orderManager;
    private KitchenLoop kitchenLoop;
    private ScoreManager scoreManager;

    public ServingCounter(String id, Position position, char symbol, StationType type, OrderManager orderManager, KitchenLoop kitchenLoop, ScoreManager scoreManager) {
        super(id, position, symbol, type);
        this.orderManager = orderManager;
        this.kitchenLoop = kitchenLoop;
        this.scoreManager = scoreManager;
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
        if(chef == null || kitchenLoop == null || orderManager == null || scoreManager == null){
            return;
        }

        Item inHand = chef.getInventory();

        if(!(inHand instanceof Plate)){
            return;
        }

        Plate plate = (Plate) inHand;

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
            scoreManager.addScore(reward);
            System.out.println("Score updated: " + scoreManager.getScore());
        } catch (OrderNotFoundException | InvalidDataException e) {
            // pinalti jika dish tidak sesuai dengan order yang ada
            scoreManager.subtractScore(20); // Contoh penalti 20 poin
            System.out.println("Incorrect dish. Penalty applied. Score: " + scoreManager.getScore());
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
            dish.addComponent(p);
        }

        return dish;
    }

    public void cleanupPlate(Plate plate){
        plate.getContents().clear();
    }
}
