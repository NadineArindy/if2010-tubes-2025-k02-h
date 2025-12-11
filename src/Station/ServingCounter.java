package src.Station;

import java.util.Set;

import src.Exception.InvalidDataException;
import src.Exception.OrderNotFoundException;
import src.Game.GameContext;
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

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
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
            GameContext.getMessenger().error("Tidak bisa serve: plate masih kotor.");
            return;
        }

        Dish dish;

        try{
            dish = builDishFromPlate(plate);
        } catch (InvalidDataException e){
            GameContext.getMessenger().error("Serve gagal: " + e.getMessage());

            cleanupPlate(plate);
            chef.setInventory(null);
            kitchenLoop.schedulePlateReturn(plate);
            return;
        }

        try {
            int reward = orderManager.processServedDish(dish);
            scoreManager.addScore(reward);
            System.out.println("Order berhasil! +" + reward + " skor. Total: " + scoreManager.getScore());
            GameContext.getMessenger().info(
                "Order berhasil! +" + reward + " skor. Total: " + scoreManager.getScore()
            );
            orderManager.spawnRandomOrder();
        } catch (OrderNotFoundException | InvalidDataException e) {
            // pinalti jika dish tidak sesuai dengan order yang ada
            int penalty = 20;
            scoreManager.subtractScore(penalty); // Contoh penalti 20 poin
            System.out.println("Dish salah pesanan! -" + penalty + " skor. Total: " + scoreManager.getScore());
            GameContext.getMessenger().error(
               "Dish salah pesanan! -" + penalty + " skor. Total: " + scoreManager.getScore()
            );
        }

        cleanupPlate(plate); // buang isi plate
        chef.setInventory(null); // tangan chef kosong
        kitchenLoop.schedulePlateReturn(plate); // piring kembali ke storage setelah 10 detik
    }

    // Mengubah isi dari plate menjadi sebuah dish
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

    // Mengosongkan isi plate
    public void cleanupPlate(Plate plate){
        plate.getContents().clear();
    }
}
