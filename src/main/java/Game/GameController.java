package Game;

import Station.*;
import Item.*;
import Order.OrderManager;
import Recipe.Recipe;

import java.util.*;

public class GameController {

    private List<Chef> chefs;
    private int activeChefIndex = 0;
    private Map<Position, Station> stationMap;
    private OrderManager orderManager;
    private KitchenLoop kitchenLoop;
    
    private String statusMessage = "";
    
    private int score = 0;
    private double levelTimer = 180.0;

    // NEW MAP SIZE
    private int width = 14;
    private int height = 10;

    public GameController() {
        chefs = new ArrayList<>();
        stationMap = new HashMap<>();
        orderManager = new OrderManager();

        // Temporary init → replaced later by correct PlateStorage
        kitchenLoop = new KitchenLoop(new PlateStorage("TMP", new Position(0,0), 'P', 10));

        loadMapA();       // ← THE NEW MAP
        setupRecipes();
        orderManager.spawnRandomOrder();
    }

    public void addScore(int value) {
        this.score += value;
    }

    public int getScore() {
        return score;
    }

    public double getLevelTimer() {
        return levelTimer;
    }

    // ===========================================================
    //             MAP TYPE A (SUSHI MAP) LOADER
    // ===========================================================
    private void loadMapA() {

        String[] MAP = {
            "XXXXXXXXXARRRA",
            "ACACACA......A",
            "A.....A......A",
            "I...V.A....V.I",
            "I..A..A......I",
            "I..A..A......A",
            "S..A..A......A",
            "S..X..T..WWP.X",
            "X........X...X",
            "XXXXXXXXXXXXXX"
        };


        for (int y = 0; y < MAP.length; y++) {
            for (int x = 0; x < MAP[y].length(); x++) {

                char c = MAP[y].charAt(x);
                Position p = new Position(x, y);

                switch (c) {

                    case 'X': addStation(new WallStation("Wall-" + x + "-" + y, p, 'X')); break;
                    case 'C': addStation(new CuttingStation("Cut-" + x, p, 'C', 1, 3000)); break;
                    
                    case 'R':
                        CookingStation stove = new CookingStation("Cook-" + x, p, 'R', 1, 12000);
                        // Distribute Utensils: Middle stove gets a Pan for Shrimp
                        if (x == 11) {
                            stove.addItem(new FryingPan("Pan"));
                        } else {
                            stove.addItem(new BoilingPot("Pot"));
                        }
                        addStation(stove);
                        break;

                    case 'A': addStation(new AssemblyStation("Asm-" + x, p, 'A', 1, 0)); break;
                    case 'S': addStation(new ServingCounter("Serve-" + x, p, 'S', orderManager, kitchenLoop, this::addScore)); break;
                    case 'T': addStation(new TrashStation("Trash-" + x, p, 'T')); break;
                    case 'W': addStation(new WashingStation("Wash-" + x, p, 'W')); break;

                    case 'I':
                        if (x == 0 && y == 3) {
                             addStation(new IngredientStorage("RiceSto", p, 'K', Rice.class, true));
                        } else if (x == 0 && y == 4) {
                             addStation(new IngredientStorage("NoriSto", p, 'N', Nori.class, true));
                        } else if (x == 0 && y == 5) {
                             addStation(new IngredientStorage("FishSto", p, 'F', Fish.class, true));
                        } else if (x == 13 && y == 3) {
                             addStation(new IngredientStorage("CukeSto", p, 'U', Cucumber.class, true));
                        } else if (x == 13 && y == 4) {
                             addStation(new IngredientStorage("ShrimpSto", p, 'E', Shrimp.class, true));
                        } else {
                             // Fallback
                             addStation(new IngredientStorage("Ing-" + x, p, 'I', Ingredient.class, true));
                        }
                        break;

                    case 'P':
                        PlateStorage ps = new PlateStorage("Plate-" + x, p, 'P', 4);
                        for (int i = 0; i < 4; i++) ps.addPlate(new Plate("Plate"));
                        addStation(ps);
                        kitchenLoop = new KitchenLoop(ps);
                        break;

                    case 'V':
                        chefs.add(new Chef("Chef-" + chefs.size(), "Chef", p));
                        break;

                    case '.':
                    default:
                        // Walkable
                        break;
                }
            }
        }
        if (!chefs.isEmpty()) activeChefIndex = 0;
    }
    // ------------------------------------------------------------
    private void addStation(Station s) {
        stationMap.put(s.getPosition(), s);
    }

    // ===========================================================
    //                 RECIPES (SUSHI MAP)
    // ===========================================================
    private void setupRecipes() {
    try {
        List<Recipe> recipes = new ArrayList<>();

        // ============================
        // 1. Kappa Maki
        // ============================
        Rice r1 = new Rice("Nasi");
        r1.setState(IngredientState.COOKED);

        Cucumber cu1 = new Cucumber("Timun");
        cu1.setState(IngredientState.CHOPPED);

        Nori no1 = new Nori("Nori");

        List<Ingredient> list1 = new ArrayList<>();
        list1.add(no1);
        list1.add(r1);
        list1.add(cu1);

        recipes.add(new Recipe("Kappa Maki", list1));


        // ============================
        // 2. Sakana Maki
        // ============================
        Rice r2 = new Rice("Nasi");
        r2.setState(IngredientState.COOKED);

        Fish f1 = new Fish("Ikan");

        Nori no2 = new Nori("Nori");

        List<Ingredient> list2 = new ArrayList<>();
        list2.add(no2);
        list2.add(r2);
        list2.add(f1);

        recipes.add(new Recipe("Sakana Maki", list2));


        // ============================
        // 3. Ebi Maki
        // ============================
        Rice r3 = new Rice("Nasi");
        r3.setState(IngredientState.COOKED);

        Shrimp sh1 = new Shrimp("Udang");
        sh1.setState(IngredientState.COOKED);

        Nori no3 = new Nori("Nori");

        List<Ingredient> list3 = new ArrayList<>();
        list3.add(no3);
        list3.add(r3);
        list3.add(sh1);

        recipes.add(new Recipe("Ebi Maki", list3));


        // ============================
        // 4. Fish Cucumber Roll
        // ============================
        Rice r4 = new Rice("Nasi");
        r4.setState(IngredientState.COOKED);

        Fish f2 = new Fish("Ikan");

        Cucumber cu2 = new Cucumber("Timun");
        cu2.setState(IngredientState.CHOPPED);

        Nori no4 = new Nori("Nori");

        List<Ingredient> list4 = new ArrayList<>();
        list4.add(no4);
        list4.add(r4);
        list4.add(f2);
        list4.add(cu2);

        recipes.add(new Recipe("Fish Cucumber Roll", list4));


        // Register recipes
        orderManager.setAvailableRecipes(recipes);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // ===========================================================
    //                   CHEF INPUT HANDLING
    // ===========================================================
    public void handleInput(Direction dir) {
        Chef chef = getActiveChef();
        if (chef.isBusy()) return;

        chef.setDirection(dir);

        Position newPos = chef.getPosition().move(dir);

        if (isValidMove(newPos)) {
            chef.setPosition(newPos);
        }
    }
    public void handleInteraction() {
        Chef chef = getActiveChef();
        if (chef.isBusy()) return;

        Position target = chef.getFacingPosition();
        Station s = stationMap.get(target);

        if (s == null) {
            setStatusMessage("No station in front.");
            return;
        }

        // before interaction (for debugging)
        Item before = chef.getInventory();

        try {
            s.interact(chef);
        } catch (RuntimeException e) {
            setStatusMessage("Error: " + e.getMessage());
            return;
        }

        // after interaction
        Item after = chef.getInventory();

        if (after != before) {
            if (after != null)
                setStatusMessage("Picked: " + after.getName());
            else
                setStatusMessage("Placed item / cleared inventory");
        } else {
            setStatusMessage("Interacted with " + s.getClass().getSimpleName());
        }
    }

    public void switchChef() {
        activeChefIndex = (activeChefIndex + 1) % chefs.size();
    }

    private boolean isValidMove(Position pos) {
        if (pos.getX() < 0 || pos.getX() >= width || pos.getY() < 0 || pos.getY() >= height) return false;
        if (stationMap.containsKey(pos)) return false;
        for (Chef c : chefs) if (c.getPosition().equals(pos)) return false;
        return true;
    }

    public Chef getActiveChef() { return chefs.get(activeChefIndex); }
    public List<Chef> getChefs() { return chefs; }
    public Map<Position, Station> getStationMap() { return stationMap; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void tick(double deltaTime) {
        int ms = (int)(deltaTime * 1000);

        if (levelTimer > 0) {
            levelTimer -= deltaTime;
            if (levelTimer < 0) levelTimer = 0;
        }

        // Check and spawn orders if needed
        if (orderManager.getActiveOrders().size() < 3) {
            orderManager.spawnRandomOrder();
        }

        // Update all stations that have timers
        for (Station s : stationMap.values()) {
            if (s instanceof CuttingStation) {
                ((CuttingStation) s).update(ms);
            }
            if (s instanceof CookingStation) {
                ((CookingStation) s).update(ms);
            }
            if (s instanceof WashingStation) {
                ((WashingStation) s).update(ms);
            }
        }

        // Update kitchen loop (plate respawn / sink cleaning)
        kitchenLoop.update(ms);

        // Handle order expiration
        int penalty = orderManager.purgeExpired();
        if (penalty > 0) {
            addScore(-penalty);
        }
    }
    public OrderManager getOrderManager() {
        return this.orderManager;
    }
    public String getStatusMessage() {
    return statusMessage;
    }

    public void setStatusMessage(String msg) {
    this.statusMessage = msg;
    }


}
