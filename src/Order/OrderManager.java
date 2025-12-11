package src.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import src.Exception.InvalidDataException;
import src.Exception.OrderNotFoundException;
import src.Item.Dish;
import src.Item.Preparable;
public class OrderManager {

    private final CopyOnWriteArrayList<Order> activeOrders = new CopyOnWriteArrayList<>();
    private int nextId = 1;
    private List<Recipe> availableRecipes;
    private int failedStreak = 0;
    public static final int DEFAULT_ORDER_TIME = 60;

    public List<Order> getActiveOrders() {
        return new ArrayList<>(activeOrders);
    }

    public Order createOrder(Recipe recipe, int reward, int penalty, int timeLimitSec) {
        Order o = new Order(nextId++, recipe, reward, penalty, timeLimitSec);
        activeOrders.add(o);
        return o;
    }

    public void removeOrder(Order o) throws OrderNotFoundException {
        if (!activeOrders.remove(o))
            throw new OrderNotFoundException("Order not found: " + o.getId());
    }

    /**
     * Cari order pertama yang match dengan dish.
     */
    public Order findMatchingOrder(Dish dish)
            throws InvalidDataException, OrderNotFoundException {
        if (dish == null) throw new InvalidDataException("Dish cannot be null");

        List<Preparable> comps = dish.getComponents();

        if (comps.isEmpty()) throw new InvalidDataException("Dish contains no components");

        for (Order o : activeOrders) {
            Recipe r = o.getRecipe();
            if (r.matches(comps))
                return o;
        }
        throw new OrderNotFoundException("No matching order");
    }

    /**
     * Saat dish di-serve:
     * - Jika cocok → order remove + return reward
     * - Jika tidak → throw
     */
    public int processServedDish(Dish dish)
            throws InvalidDataException, OrderNotFoundException {

        Order matched = findMatchingOrder(dish);
        removeOrder(matched);
        return matched.getReward();
    }

    /**
     * Buang order expire → return jumlah order yang expired
     */
    
    // Di OrderManager.java
    public int purgeExpired() {
        int totalPenalty = 0;
        for (Order o : activeOrders) {
            if (o.isExpired()) {
                activeOrders.remove(o);
                totalPenalty += o.getPenalty(); // Jumlahkan penaltinya
                spawnRandomOrder(); // Spawn order baru sebagai pengganti
            }
        }
        return totalPenalty;
    }

    // public int purgeExpired() {
    //     int removed = 0;
    //     for (Order o : activeOrders) {
    //         if (o.isExpired()) {
    //             activeOrders.remove(o);
    //             removed++;
    //         }
    //     }
    //     return removed;
    // }

    public void setAvailableRecipes(List<Recipe> recipes) {
        this.availableRecipes = recipes;
    }

    public void spawnRandomOrder() {
        if (availableRecipes == null || availableRecipes.isEmpty()) return;
        
        Random rand = new Random();
        Recipe randomRecipe = availableRecipes.get(rand.nextInt(availableRecipes.size()));
        
        // Nilai reward/time limit bisa hardcode atau ambil dari properti Recipe jika ada
        int reward = 100; // Contoh
        int penalty = 50; 
        int timeLimit = DEFAULT_ORDER_TIME;

        createOrder(randomRecipe, reward, penalty, timeLimit);
    }

    public List<Recipe> getAvailableRecipes() {
        return availableRecipes;
    }

    //Cari nama resep yang cocok dengan komponen dish.
    public String findMatchingRecipeName(List<Preparable> comps) {
        if (availableRecipes == null || comps == null) return null;

        for (Recipe r : availableRecipes) {
            if (r.matches(comps)) {
                return r.getName();
            }
        }
        return null;
    }

    public int getFailedStreak() {
        return failedStreak;
    }

    public void registerFailedOrder() {
        failedStreak++;
    }

    public void registerCompletedOrder() {
        failedStreak = 0; // reset jika ada order yang sukses
    }

}