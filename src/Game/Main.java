package src.Game;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import src.Station.PlateStorage;
import src.Station.ServingCounter;
import src.Station.Station;
import src.Station.KitchenLoop;
import src.Ingredients.Ingredient;
import src.Ingredients.IngredientState;
import src.Ingredients.Nori;
import src.Ingredients.Rice;
import src.Ingredients.Shrimp;
import src.Ingredients.Cucumber;
import src.Ingredients.Fish;
import src.Order.OrderManager;
import src.Order.Recipe;
import src.chef.Chef;
import src.chef.Position;
import src.chef.Direction;

public class Main {
    public static void main(String[] args) {
        // ===================== SETUP MAP & CHEF =====================

        //Buat map game
        GameMap map = MapFactory.createSampleMap();

        //Spawn dua chef
        Position spawn = map.getSpawnPoint();
        Chef chefA = new Chef("C1", "Chef Nadine", new Position(spawn.getX() + 1, spawn.getY()));
        Chef chefB = new Chef("C2", "Chef Riko", new Position(spawn.getX() - 5, spawn.getY()));

        //Game controller untuk switch chef
        GameController controller = new GameController(chefA, chefB);
        Chef[] allChefs = new Chef[]{chefA, chefB};

        // ===================== SCORE, ORDER, STAGE =====================
        
        ScoreManager scoreManager = new ScoreManager();
        OrderManager orderManager = new OrderManager();
        // Simpan di GameContext supaya class lain bisa akses
        GameContext.setOrderManager(orderManager); 
        
        StageConfig stage1 = null;
        try {
            // ====== DAFTAR RESEP ======
            // Nasi (COOKED)
            Rice riceCooked = new Rice("Rice");
            riceCooked.setState(IngredientState.COOKED);

            // Nori (RAW)
            Nori noriRaw = new Nori("Nori");
            noriRaw.setState(IngredientState.RAW);

            // Timun (CHOPPED)
            Cucumber cucumberChopped = new Cucumber("Cucumber");
            cucumberChopped.setState(IngredientState.CHOPPED);

            // Ikan (CHOPPED)
            Fish fishChopped = new Fish("Fish");
            fishChopped.setState(IngredientState.CHOPPED);

            // Udang (CHOPPED -> COOKED)
            Shrimp shrimpCooked = new Shrimp("Shrimp");
            shrimpCooked.setState(IngredientState.COOKED);

            // === Kappa Maki: Nori (RAW) + Nasi (COOKED) + Timun (CHOPPED) ===
            List<Ingredient> kappaComps = new ArrayList<>();
            kappaComps.add(copyOf(noriRaw));
            kappaComps.add(copyOf(riceCooked));
            kappaComps.add(copyOf(cucumberChopped));
            Recipe kappaMaki = new Recipe("Kappa Maki", kappaComps);

            // === Sakana Maki: Nori (RAW) + Nasi (COOKED) + Ikan (CHOPPED) ===
            List<Ingredient> sakanaComps = new ArrayList<>();
            sakanaComps.add(copyOf(noriRaw));
            sakanaComps.add(copyOf(riceCooked));
            sakanaComps.add(copyOf(fishChopped));
            Recipe sakanaMaki = new Recipe("Sakana Maki", sakanaComps);

            // === Ebi Maki: Nori (RAW) + Nasi (COOKED) + Udang (COOKED) ===
            List<Ingredient> ebiComps = new ArrayList<>();
            ebiComps.add(copyOf(noriRaw));
            ebiComps.add(copyOf(riceCooked));
            ebiComps.add(copyOf(shrimpCooked));
            Recipe ebiMaki = new Recipe("Ebi Maki", ebiComps);

            // === Fish Cucumber Roll: Nori (RAW) + Nasi (COOKED) + Ikan (CHOPPED) + Timun (CHOPPED) ===
            List<Ingredient> fishCucumberComps = new ArrayList<>();
            fishCucumberComps.add(copyOf(noriRaw));
            fishCucumberComps.add(copyOf(riceCooked));
            fishCucumberComps.add(copyOf(fishChopped));
            fishCucumberComps.add(copyOf(cucumberChopped));
            Recipe fishCucumberRoll = new Recipe("Fish Cucumber Roll", fishCucumberComps);

            // Masukkan semua resep ke OrderManager
            List<Recipe> recipes = List.of(
                kappaMaki,
                sakanaMaki,
                ebiMaki,
                fishCucumberRoll
            );

            orderManager.setAvailableRecipes(recipes);

            // === KONFIGURASI STAGE 1 ===
            stage1 = new StageConfig(
                "Stage 1 - Sushi Bar",
                180,   // durasi 3 menit
                300,       // target score 
                5,     // max gagal beruntun
                3, // max order aktif sekaligus
                recipes
            );

            // Spawn beberapa order awal
            orderManager.spawnRandomOrder();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // ===================== SETUP WINDOW & PANEL =====================

        // Window Utama
        JFrame frame = new JFrame("Nimonscooked");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panel untuk menggambar map + chef + HUD
        MapPanel panel = new MapPanel(map, allChefs, controller.getActiveChef(), scoreManager, orderManager);
        frame.add(panel, BorderLayout.CENTER);

        // Label status di bagian bawah (buat pesan info/error singkat)
        JLabel statusLabel = new JLabel("Ready");
        frame.add(statusLabel, BorderLayout.SOUTH);

        // ===================== GAME CONTEXT (MESSENGER) =====================

        GameContext.setMessenger(new GameContext.GameMessenger() {
            @Override
            public void info(String msg) {
                statusLabel.setForeground(Color.BLACK);
                statusLabel.setText(msg);
                System.out.println(msg);
            }

            @Override
            public void error(String msg) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("!!! " + msg);
                System.err.println(msg);
            }
        });

        // Atur ukuran dan tampilkan frame
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // ===================== PLATE STORAGE, SERVING COUNTER, KITCHEN LOOP =====================
        
        PlateStorage plateStorage = null;
        List<ServingCounter> servingCounters = new ArrayList<>();

        // Cari PlateStorage dan semua ServingCounter yang ada di map
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Tile tile = map.getTileAt(x, y);
                if (tile == null || !tile.hasStation()) continue;
                Station s = tile.getStation();

                if (s instanceof PlateStorage && plateStorage == null) {
                    plateStorage = (PlateStorage) s;
                } else if (s instanceof ServingCounter sc) {
                    servingCounters.add(sc);
                }
            }
        }

        // Buat KitchenLoop
        KitchenLoop kitchenLoop = null;
        if (plateStorage != null) {
            kitchenLoop = new KitchenLoop(plateStorage, scoreManager);
        }

        // Hubungkan setiap ServingCounter dengan OrderManager, KitchenLoop, dan ScoreManager
        if (!servingCounters.isEmpty()) {
            for (ServingCounter servingCounter : servingCounters) {
                servingCounter.setOrderManager(orderManager);
                servingCounter.setKitchenLoop(kitchenLoop);
                servingCounter.setScoreManager(scoreManager);
            }
        }
        
        // Jika tadi terjadi error saat buat stage, siapkan stage cadangan (fallback)
        if (stage1 == null) {
            stage1 = new StageConfig(
                "Fallback Stage",
                180,
                0,
                999,
                3,
                orderManager.getAvailableRecipes()
            );
        }

        // ===================== GAME LOOP =====================

        GameLoop gameLoop = new GameLoop(map, kitchenLoop, orderManager, scoreManager, stage1);
        panel.setGameLoop(gameLoop);

        // ===================== INPUT HANDLER =====================

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Chef active = controller.getActiveChef();
                Chef[] others = new Chef[]{controller.getInactiveChef()};

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_B: // Switch chef  
                        controller.switchChef();
                        panel.setActiveChef(controller.getActiveChef());
                        break;
                    case KeyEvent.VK_W: // move up
                        active.move(Direction.UP, map, others);
                        break;
                    case KeyEvent.VK_S: // move down
                        active.move(Direction.DOWN, map, others);
                        break;
                    case KeyEvent.VK_A: // move left
                        active.move(Direction.LEFT, map, others);
                        break;
                    case KeyEvent.VK_D: // move right
                        active.move(Direction.RIGHT, map, others);
                        break;
                    case KeyEvent.VK_E: // interact
                        active.interact(map);
                        break;
                }

                panel.repaint();
            }
        });

        // ===================== GAME TIMER (TICK) =====================

        final int TICK_MS = 100; // 0.1 detik per tick

        // Timer Swing yang memanggil gameLoop.update()
        Timer gameTimer = new Timer(TICK_MS, e -> {
            gameLoop.update(TICK_MS);   // update game loop
            panel.repaint();

            // Cek apakah stage sudah berakhir
            if (gameLoop.isStageOver()) {
                ((Timer) e.getSource()).stop();

                String title;
                String message;

                if (gameLoop.isStagePass()) {
                        // === STAGE PASS ===
                        title = "Stage Clear!";
                        message = "Stage Clear!\n"
                                + "Score: " + scoreManager.getScore() + "\n"
                                + "Nice job, Chef!";
                } else if (gameLoop.isFailedByTooManyOrders()) {
                        // === FAIL: TOO MANY FAILED ORDERS ===
                        title = "Stage Failed";
                        message = "Stage Failed - Too many failed orders!\n"
                                + "Score: " + scoreManager.getScore() + "\n"
                                + "Be more careful with the orders!";
                } else {
                        // === FAIL: TIME'S UP ===
                        title = "Stage Failed";
                        message = "Time's up!\n"
                                + "Score: " + scoreManager.getScore() + "\n"
                                + "You didn't reach the target score.";
                }

                JOptionPane.showMessageDialog(
                        frame,
                        message,
                        title,
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        gameTimer.start();

    }

    /** 
     * Membuat salinan (copy) dari sebuah ingredient.
     * Digunakan untuk menduplikasi ingredient pada resep.
     */
    private static Ingredient copyOf(Ingredient src) {
    try {
        Ingredient copy = src.getClass()
                .getDeclaredConstructor(String.class)
                .newInstance(src.getName());
        copy.setState(src.getState());
        return copy;
    } catch (Exception e) {
        throw new RuntimeException("Failed to copy ingredient " + src.getName(), e);
    }
}

}
