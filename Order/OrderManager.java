package Order;

import Item.Dish;
import Item.Ingredient;
import Exception.InvalidDataException;
import Exception.OrderNotFoundException;
import Recipe.Recipe;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrderManager {
    private final CopyOnWriteArrayList<Order> orders = new CopyOnWriteArrayList<>();
    private int nextPos = 1;

    public List<Order> getOrders() { 
        return List.copyOf(orders); 
    }

    public Order createOrder(Recipe r, int reward, int penalty, int timeLimitSec) {
        Order o = new Order(nextPos++, r, reward, penalty, timeLimitSec);
        orders.add(o);
        return o;
    }

    public void addOrder(Order o) { orders.add(o); }

    public void removeOrder(Order o) throws OrderNotFoundException {
        if (!orders.remove(o)) throw new OrderNotFoundException("Order not found: " + o);
    }

    public Order findMatching(Dish dish) throws InvalidDataException, OrderNotFoundException {
        if (dish == null) throw new InvalidDataException("dish null");
        List<Ingredient> comps = dish.getComponents();
        if (comps.isEmpty()) throw new InvalidDataException("dish empty");
        for (Order o : orders) {
            Recipe r = o.getRecipe();
            if (r.matches(comps)) return o;
        }
        throw new OrderNotFoundException("no matching order");
    }

    public int processServedDish(Dish dish) throws InvalidDataException, OrderNotFoundException {
        Order match = findMatching(dish);
        removeOrder(match);
        return match.getReward();
    }

    public int purgeExpired() {
        int removed = 0;
        for (Order o : new ArrayList<>(orders)) {
            if (o.isExpired()) { 
                orders.remove(o); removed++; 
            }
        }
        return removed;
    }
}