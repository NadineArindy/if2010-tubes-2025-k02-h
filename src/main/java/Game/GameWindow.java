package Game;

import Station.*;
import Item.*;
import Order.Order;
import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Set;
import java.util.List;

public class GameWindow extends Application {

    private GameController controller;
    private static final int TILE_SIZE = 64;
    private static final int UI_HEIGHT = 60; // Reduced height since orders move to side
    private static final int SIDEBAR_WIDTH = 220; // Width for the Order Sidebar
    private Canvas canvas;

    @Override
    public void start(Stage primaryStage) {
        controller = new GameController();
        
        int mapWidth = controller.getWidth() * TILE_SIZE;
        int mapHeight = controller.getHeight() * TILE_SIZE;
        
        // Canvas sized for Map + Sidebar + Top UI
        // Total Width = Map + Sidebar
        // Total Height = UI + Map
        int totalWidth = mapWidth + SIDEBAR_WIDTH;
        int totalHeight = mapHeight + UI_HEIGHT;
        
        canvas = new Canvas(totalWidth, totalHeight);
        
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, totalWidth, totalHeight);
        
        // Input Handling
        scene.setOnKeyPressed(this::handleInput);
        root.setFocusTraversable(true);
        root.requestFocus();
        
        primaryStage.setTitle("Sushi Overcooked (JavaFX)");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Game Loop
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = System.nanoTime();

            @Override
            public void handle(long now) {
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                
                controller.tick(deltaTime);
                render(canvas.getGraphicsContext2D());
            }
        };
        timer.start();
    }

    private void handleInput(KeyEvent e) {
        switch (e.getCode()) {
            case W: controller.handleInput(Direction.UP); break;
            case S: controller.handleInput(Direction.DOWN); break;
            case A: controller.handleInput(Direction.LEFT); break;
            case D: controller.handleInput(Direction.RIGHT); break;
            case E: controller.handleInteraction(); break;
            case TAB: controller.switchChef(); break;
            default: break;
        }
    }

    private void render(GraphicsContext gc) {
        // Clear entire background
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 1. Render Top UI (Score, Time, Status)
        renderTopUI(gc);

        // 2. Render Map (Below UI, Left Side)
        gc.save();
        gc.translate(0, UI_HEIGHT);
        renderMap(gc);
        gc.restore();

        // 3. Render Order Sidebar (Right Side, Full Height)
        gc.save();
        gc.translate(controller.getWidth() * TILE_SIZE, 0); // Move to the right of the map
        renderOrders(gc);
        gc.restore();
    }

    private void renderTopUI(GraphicsContext gc) {
        // Background
        gc.setFill(Color.rgb(50, 50, 50));
        gc.fillRect(0, 0, canvas.getWidth(), UI_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Score & Time
        gc.fillText(String.format("Score: %d", controller.getScore()), 20, 35);
        gc.fillText(String.format("Time: %.1f", controller.getLevelTimer()), 150, 35);
        
        // Status Message
        gc.setFont(Font.font("Arial", 14));
        gc.fillText("Status: " + controller.getStatusMessage(), 300, 35);
    }

    private void renderOrders(GraphicsContext gc) {
        // Sidebar Background
        gc.setFill(Color.rgb(40, 40, 40));
        gc.fillRect(0, 0, SIDEBAR_WIDTH, canvas.getHeight());

        // Title
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("ORDERS", 20, 30);
        
        // Separator
        gc.setStroke(Color.GRAY);
        gc.strokeLine(10, 40, SIDEBAR_WIDTH - 10, 40);

        int startY = 50;
        int gap = 110; // Height per order box

        for (Order order : controller.getOrderManager().getActiveOrders()) {
            // Draw Order Box
            gc.setFill(Color.rgb(255, 250, 205)); // LemonChiffon
            gc.fillRect(10, startY, SIDEBAR_WIDTH - 20, gap - 10);
            gc.setStroke(Color.ORANGE);
            gc.strokeRect(10, startY, SIDEBAR_WIDTH - 20, gap - 10);

            // Recipe Name
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            gc.fillText(order.getRecipe().getName(), 15, startY + 20);

            // Timer & Reward
            gc.setFont(Font.font("Arial", 12));
            gc.fillText(String.format("Time: %.0fs   +$%d", order.getTimeLeft(), order.getReward()), 15, startY + 35);
            
            // Ingredients List
            gc.setFont(Font.font("Arial", 10));
            int ingY = startY + 50;
            List<Ingredient> required = order.getRecipe().getRequiredIngredients();
            for (Ingredient ing : required) {
                // Shorten state names
                String state = "";
                if (ing.getState() == IngredientState.COOKED) state = "(Ck)";
                else if (ing.getState() == IngredientState.CHOPPED) state = "(Ch)";
                else if (ing.getState() == IngredientState.RAW) state = "(Raw)";
                
                gc.fillText("- " + ing.getName() + " " + state, 15, ingY);
                ingY += 12;
            }

            // Progress Bar (Time)
            double maxTime = 60.0; // Estimate
            double pct = Math.max(0, order.getTimeLeft() / maxTime);
            gc.setFill(Color.RED);
            gc.fillRect(15, startY + gap - 18, (SIDEBAR_WIDTH - 30) * pct, 5);
            
            startY += gap;
        }
    }

    private void renderMap(GraphicsContext gc) {
        // Grid
        for (int x = 0; x < controller.getWidth(); x++) {
            for (int y = 0; y < controller.getHeight(); y++) {
                gc.setFill(Color.LIGHTGRAY);
                gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                gc.setStroke(Color.WHITE);
                gc.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // Stations
        for (Map.Entry<Position, Station> entry : controller.getStationMap().entrySet()) {
            Position pos = entry.getKey();
            Station station = entry.getValue();
            drawStation(gc, pos.getX(), pos.getY(), station);
        }

        // Chefs
        for (Chef chef : controller.getChefs()) {
            drawChef(gc, chef);
        }
    }

    private void drawStation(GraphicsContext gc, int x, int y, Station station) {
        double screenX = x * TILE_SIZE;
        double screenY = y * TILE_SIZE;

        Color c = Color.GRAY;
        String symbol = String.valueOf(station.getSymbol());

        if (station instanceof IngredientStorage) c = Color.SADDLEBROWN;
        else if (station instanceof CuttingStation) c = Color.SANDYBROWN;
        else if (station instanceof CookingStation) c = Color.FIREBRICK;
        else if (station instanceof AssemblyStation) c = Color.BURLYWOOD;
        else if (station instanceof PlateStorage) c = Color.IVORY;
        else if (station instanceof ServingCounter) c = Color.DARKGREEN;
        else if (station instanceof TrashStation) c = Color.BLACK;
        else if (station instanceof WashingStation) c = Color.CORNFLOWERBLUE;
        else if (station instanceof WallStation) c = Color.DARKSLATEGRAY;

        gc.setFill(c);
        gc.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(screenX, screenY, TILE_SIZE, TILE_SIZE);

        // Symbol (Top-Left)
        gc.setFill(station instanceof PlateStorage ? Color.BLACK : Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText(symbol, screenX + 5, screenY + 15);

        // Items on top
        Item item = null;
        if (station instanceof Workstation) {
            item = ((Workstation) station).peekTopItem();
        } else if (station instanceof IngredientStorage) {
            item = ((IngredientStorage) station).getItemOnTop();
        } else if (station instanceof PlateStorage) {
             if (!((PlateStorage) station).isEmpty()) item = ((PlateStorage) station).peekPlate();
        }
        
        if (item != null) {
            drawItem(gc, screenX + TILE_SIZE/4, screenY + TILE_SIZE/4, item);
        }

        // Progress Bars
        drawProgressBar(gc, screenX, screenY, station);
    }

    private void drawProgressBar(GraphicsContext gc, double x, double y, Station station) {
        double progress = 0;
        Color barColor = Color.GREEN;
        boolean show = false;

        if (station instanceof CuttingStation) {
            CuttingStation cs = (CuttingStation) station;
            if (cs.isCutting()) {
                progress = 1.0 - (double)cs.getRemainingTime() / CuttingStation.CUTTING_TIME;
                barColor = Color.BLUE;
                show = true;
            }
        } else if (station instanceof CookingStation) {
            CookingStation cs = (CookingStation) station;
            if (cs.isCooking()) {
                // 12s total time. Counts UP. Bar empties or fills?
                // Let's make it fill up (Progress).
                progress = (double)cs.getRemainingTime() / 12000.0;
                barColor = Color.RED;
                show = true;
            }
        } else if (station instanceof WashingStation) {
            WashingStation ws = (WashingStation) station;
            if (ws.isWashing()) {
                progress = 1.0 - (double)ws.getRemainingWashTime() / WashingStation.WASH_TIME;
                barColor = Color.CYAN;
                show = true;
            }
        }

        if (show) {
            gc.setFill(Color.GRAY);
            gc.fillRect(x + 5, y + 5, TILE_SIZE - 10, 5);
            gc.setFill(barColor);
            gc.fillRect(x + 5, y + 5, (TILE_SIZE - 10) * Math.min(Math.max(progress, 0), 1), 5);
        }
    }

    private void drawChef(GraphicsContext gc, Chef chef) {
        double x = chef.getPosition().getX() * TILE_SIZE;
        double y = chef.getPosition().getY() * TILE_SIZE;

        if (chef == controller.getActiveChef()) {
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(3);
            gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
            gc.setLineWidth(1);
        }

        gc.setFill(Color.ROYALBLUE);
        gc.fillOval(x + 5, y + 5, TILE_SIZE - 10, TILE_SIZE - 10);

        Item inventory = chef.getInventory();
        if (inventory != null) {
            drawItem(gc, x + TILE_SIZE/3, y + TILE_SIZE/3, inventory);
        }
    }

    private Color getIngredientColor(String name) {
        if (name.contains("Rice")) return Color.WHITESMOKE;
        if (name.contains("Fish")) return Color.SALMON;
        if (name.contains("Nori")) return Color.BLACK;
        if (name.contains("Cucumber")) return Color.GREEN;
        if (name.contains("Shrimp")) return Color.PINK;
        return Color.GREEN;
    }

    private void drawItem(GraphicsContext gc, double x, double y, Item item) {
        if (item instanceof Plate) {
            gc.setFill(Color.WHITE);
            gc.fillOval(x, y, TILE_SIZE/2.5, TILE_SIZE/2.5);
            gc.setStroke(Color.BLACK);
            gc.strokeOval(x, y, TILE_SIZE/2.5, TILE_SIZE/2.5);

            Set<Preparable> contents = ((Plate) item).getContents();
            if (!contents.isEmpty()) {
                int i = 0;
                double miniSize = TILE_SIZE / 6.0;
                for (Preparable p : contents) {
                    if (p instanceof Ingredient) {
                        gc.setFill(getIngredientColor(((Ingredient) p).getName()));
                        // Arranging in a small cluster
                        double offX = (i % 2) * miniSize + 5;
                        double offY = (i / 2) * miniSize + 5;
                        gc.fillOval(x + offX, y + offY, miniSize, miniSize);
                        i++;
                        if (i >= 4) break; // Limit viz to 4 items
                    }
                }
            }

        } else if (item instanceof Ingredient) {
            String name = ((Ingredient) item).getName();
            gc.setFill(getIngredientColor(name));
            gc.fillRect(x, y, TILE_SIZE/2.5, TILE_SIZE/2.5);
            
            IngredientState state = ((Ingredient) item).getState();
            if (state == IngredientState.COOKED) {
                 gc.setStroke(Color.RED);
                 gc.strokeRect(x, y, TILE_SIZE/2.5, TILE_SIZE/2.5);
            } else if (state == IngredientState.CHOPPED) {
                 gc.setStroke(Color.BLUE);
                 gc.strokeRect(x, y, TILE_SIZE/2.5, TILE_SIZE/2.5);
            } else if (state == IngredientState.BURNED) {
                 gc.setStroke(Color.BLACK);
                 gc.setLineWidth(2);
                 gc.strokeRect(x, y, TILE_SIZE/2.5, TILE_SIZE/2.5);
                 gc.setLineWidth(1);
            }

        } else if (item instanceof KitchenUtensils) {
            if (item instanceof BoilingPot) {
                gc.setFill(Color.DARKSLATEGRAY);
                gc.fillOval(x, y, TILE_SIZE/2.5, TILE_SIZE/2.5);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(x, y, TILE_SIZE/2.5, TILE_SIZE/2.5);
                // Lid/Rim
                gc.strokeOval(x+2, y+2, TILE_SIZE/2.5 - 4, TILE_SIZE/2.5 - 4);

            } else if (item instanceof FryingPan) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(3);
                // Handle
                gc.strokeLine(x + TILE_SIZE/2.5, y + TILE_SIZE/2.5, x + TILE_SIZE/2.0 + 5, y + TILE_SIZE/2.0 + 5);
                gc.setLineWidth(1);
                gc.setFill(Color.BLACK);
                gc.fillOval(x, y, TILE_SIZE/2.5, TILE_SIZE/2.5);

            } else {
                gc.setFill(Color.LIGHTSLATEGRAY);
                gc.fillRect(x, y, TILE_SIZE/2.5, TILE_SIZE/2.5);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Arial", 10));
                gc.fillText("U", x + 2, y + 12);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}