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

    // ======= GLOBAL UI & STATE =======
    private static JFrame frame;
    private static MenuPanel menuPanel;
    private static GameState gameState;
    private static JLabel statusLabel;

    // ======= KONFIGURAASI STAGE =======
    private static StageConfig easyStage;
    private static StageConfig hardStage;
    private static StageConfig stageConfig;
    private static StageConfig lastStageConfig;

    // ======= PROGRESS STAGE =======
    static boolean easyCleared  = false;
    static boolean hardCleared  = false;

    // ======= OBJEK GAMEPLAY =======
    private static GameMap map;
    private static GameLoop gameLoop;
    private static Timer gameTimer;
    private static ScoreManager scoreManager;
    private static OrderManager orderManager;
    private static GameController controller;
    private static MapPanel mapPanel;
    private static Chef[] allChefs;

    // ======= MUSIK =======
    private static MusicPlayer bgm;

    public static void main(String[] args) {
        //frame + menuPanel
        initFrameAndMenu();

        //recipe + stageConfig (easy & hard)
        setupStagesAndRecipes();   

        //input handler
        installGlobalKeyHandler();

        //main menu
        switchToMainMenu();
    }

    // ======= JFRAME + MENU PANEL + STATUS LABEL =======
    private static void initFrameAndMenu() {
        frame = new JFrame("Nimonscooked");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // panel untuk tampilan menu (main, help, stage select, post-stage)
        menuPanel = new MenuPanel();
        frame.add(menuPanel, BorderLayout.CENTER);

        // status bar di bawah saat state IN GAME
        statusLabel = new JLabel("Ready");
        frame.add(statusLabel, BorderLayout.SOUTH);

        // Messenger global (game context)
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

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ===================== SCORE, ORDER, STAGE =====================
    private static void setupStagesAndRecipes() {
        scoreManager = new ScoreManager();
        orderManager = new OrderManager();
        // Simpan di GameContext supaya class lain bisa akses
        GameContext.setOrderManager(orderManager); 

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

            // === KONFIGURASI STAGE EASY ===
            easyStage = new StageConfig(
                "Stage 1 - EASY",
                240,   // durasi 4 menit
                300,       // target score 
                3,     // max gagal beruntun
                3, // max order aktif sekaligus
                recipes
            );

            // === KONFIGURASI STAGE HARD ===
            hardStage = new StageConfig(
                "Stage 1 - HARD",
                180,   // durasi 3 menit
                400,       // target score 
                2,     // max gagal beruntun
                3, // max order aktif sekaligus
                recipes
            );

            // default stage awal 
            stageConfig = easyStage;

        } catch (Exception ex) {
            ex.printStackTrace();
            // fallback 
            easyStage = new StageConfig(
                "Fallback Stage",
                180,
                0,
                999,
                3,
                orderManager.getAvailableRecipes()
            );
            hardStage = easyStage;
            stageConfig = easyStage;
        }

    }

    // ======= START NEW STAGE (EASY/HARD) =======
    private static void startStage(StageConfig config) {
        // STOP music lainnya dulu
        if (bgm != null) bgm.stop();

        // PLAY TITLE MUSIC
        bgm = new MusicPlayer();
        bgm.playLoop("resources/assets/music/game.wav");

        // matikan timer stage sebelumnya
        if (gameTimer != null) gameTimer.stop();

        // menyimpan stage yang sedang di mainkan 
        stageConfig = config;
        // untuk retry
        lastStageConfig = config;

        // reset stage
        orderManager.resetStageStats();
        orderManager.clearAllOrders();


        // === SETUP MAP & CHEF ===
        map = MapFactory.createSampleMap();

        Position spawn = map.getSpawnPoint();
        Chef chefA = new Chef("C1", "Chef Nadine", new Position(spawn.getX() + 1, spawn.getY()));
        Chef chefB = new Chef("C2", "Chef Riko", new Position(spawn.getX() - 5, spawn.getY()));

        controller = new GameController(chefA, chefB);
        allChefs = new Chef[]{chefA, chefB};

        // MapPanel baru
        mapPanel = new MapPanel(map, allChefs, controller.getActiveChef(),
                                scoreManager, orderManager);

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
        if (stageConfig == null) {
            stageConfig = new StageConfig(
                "Fallback Stage",
                180,
                0,
                999,
                3,
                orderManager.getAvailableRecipes()
            );
        }

        // GameLoop baru
        gameLoop = new GameLoop(map, kitchenLoop, orderManager, scoreManager, stageConfig);
        mapPanel.setGameLoop(gameLoop);

        // Spawn order awal tiap mulai stage
        orderManager.spawnRandomOrder();

        // Ganti panel ke MapPanel (gamestate: IN_GAME)
        gameState = GameState.IN_GAME;
        frame.getContentPane().removeAll();
        frame.add(mapPanel, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);
        frame.revalidate();
        frame.repaint();

        // Timer
        final int TICK_MS = 100;
        gameTimer = new Timer(TICK_MS, e -> {
            gameLoop.update(TICK_MS);
            mapPanel.repaint();

            if (gameLoop.isStageOver()) {
                ((Timer) e.getSource()).stop();

                String resultText;
                if (gameLoop.isStagePass()) {
                    resultText = "PASS";
                    if (config == easyStage) easyCleared = true;
                    if (config == hardStage) hardCleared = true;
                } else if (gameLoop.isFailedByTooManyOrders()) {
                    resultText = "Too Many Failed Orders";
                } else {
                    resultText = "Time's Up";
                }

                switchToPostStage(resultText);
            }
        });
        gameTimer.start();
    }

    // ======= SWITCH ANTAR GAME STATE =======
    // Menampilkan Main Menu
    private static void switchToMainMenu() {
        // STOP music lainnya dulu
        if (bgm != null) bgm.stop();

        // PLAY TITLE MUSIC
        bgm = new MusicPlayer();
        bgm.playLoop("resources/assets/music/title.wav");

        gameState = GameState.MAIN_MENU;
        frame.getContentPane().removeAll();
        frame.add(menuPanel, BorderLayout.CENTER);

        menuPanel.setStageProgress(easyCleared, hardCleared);
        menuPanel.setState(GameState.MAIN_MENU);

        frame.revalidate();
        frame.repaint();   
    }

    // Menampilkan Halaman Help
    private static void switchToHelp()      {
        gameState = GameState.HELP;
        frame.getContentPane().removeAll();
        frame.add(menuPanel, BorderLayout.CENTER);

        menuPanel.setState(GameState.HELP);

        frame.revalidate();
        frame.repaint();
    }

    // Menampilkan Stage Select
    private static void switchToStageSelect(){
        gameState = GameState.STAGE_SELECT;
        frame.getContentPane().removeAll();
        frame.add(menuPanel, BorderLayout.CENTER);

        menuPanel.setStageProgress(easyCleared, hardCleared);
        menuPanel.setState(GameState.STAGE_SELECT);

        frame.revalidate();
        frame.repaint();
    }

    // Menampilkan Result Screen
    private static void switchToPostStage(String resultText) {
        gameState = GameState.POST_STAGE;
        frame.getContentPane().removeAll();
        frame.add(menuPanel, BorderLayout.CENTER);

        menuPanel.setStageProgress(easyCleared, hardCleared);
        menuPanel.setLastResult(resultText, scoreManager.getScore(), orderManager.getSuccessCount(), orderManager.getFailedCount());
        menuPanel.setState(GameState.POST_STAGE);

        frame.revalidate();
        frame.repaint();
    }

    // ======= INPUT HANDLER GLOBAL =======
    private static void installGlobalKeyHandler() {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (gameState) {
                    case MAIN_MENU   -> handleMainMenuKey(e);
                    case HELP        -> handleHelpKey(e);
                    case STAGE_SELECT-> handleStageSelectKey(e);
                    case IN_GAME     -> handleInGameKey(e);  
                    case POST_STAGE  -> handlePostStageKey(e);
                }
            }
        });
    }

    /* MAIN MENU: 
    * - X = Start -> Stage Select
    * - H = Help
    * - Q = Exit
    */ 
    private static void handleMainMenuKey(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_X -> switchToStageSelect();
            case KeyEvent.VK_H -> switchToHelp();
            case KeyEvent.VK_Q -> System.exit(0);
        }
    }

    /* HELP:
    * - ESC = back to main menu
     */
    private static void handleHelpKey(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            switchToMainMenu();
        }
    }

    /* STAGE SELECT:
    * - 1 = easyStage
    * - 2 = hardStage
    * - ESC = back to main menu
    */
    private static void handleStageSelectKey(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1 -> startStage(easyStage);
            case KeyEvent.VK_2 -> startStage(hardStage);
            case KeyEvent.VK_ESCAPE -> switchToMainMenu();
        }
    }

    /* IN GAME:
    * - W/A/S/D = easyStage
    * - E = interact
    * - B = switch chef
    */
    private static void handleInGameKey(KeyEvent e) {
        Chef active = controller.getActiveChef();
        Chef[] others = new Chef[]{controller.getInactiveChef()};

        switch (e.getKeyCode()) {
            case KeyEvent.VK_B -> {
                controller.switchChef();
                mapPanel.setActiveChef(controller.getActiveChef());
            }
            case KeyEvent.VK_W -> active.move(Direction.UP, map, others);
            case KeyEvent.VK_S -> active.move(Direction.DOWN, map, others);
            case KeyEvent.VK_A -> active.move(Direction.LEFT, map, others);
            case KeyEvent.VK_D -> active.move(Direction.RIGHT, map, others);
            case KeyEvent.VK_E -> active.interact(map);
        }

        mapPanel.repaint();
    }

    /* POST STAGE:
    * - R = retry stage terakhir
    * - N = back to select stage
    */
    private static void handlePostStageKey(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_R -> {
                if (lastStageConfig != null) {
                    startStage(lastStageConfig);
                }
            }
            case KeyEvent.VK_N -> switchToStageSelect();
        }
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

