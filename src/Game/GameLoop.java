package src.Game;

import java.util.ArrayList;
import java.util.List;

import src.Station.CuttingStation;
import src.Station.CookingStation;
import src.Station.WashingStation;
import src.Station.Station;
import src.Order.OrderManager;
import src.Station.KitchenLoop;

/**
 * GameLoop:
 * - Update semua station yang pakai waktu (cutting / cooking / washing)
 * - Update KitchenLoop (piring yang sudah di-serve balik lagi)
 * - Menghapus order yang sudah kadaluarsa + beri penalty ke score
 * - Mengatur timer stage (sisa waktu) dan hasil akhir PASS / FAIL
 */
public class GameLoop {
    private final GameMap map;

    // Daftar station yang perlu di-update
    private final List<CuttingStation> cuttingStations = new ArrayList<>();
    private final List<CookingStation> cookingStations = new ArrayList<>();
    private final List<WashingStation> washingStations = new ArrayList<>();

    private final KitchenLoop kitchenLoop;
    private final OrderManager orderManager;
    private final ScoreManager scoreManager;

    // ====== STAGE & TIMER ======
    private final StageConfig stageConfig;
    private long remainingTimeMs = 0L; // sisa waktu stage
    private boolean stageOver = false;
    private boolean stagePass = false;
    private boolean failedByTooManyOrders = false;

    public GameLoop(GameMap map, KitchenLoop kitchenLoop, OrderManager orderManager, ScoreManager scoreManager, StageConfig stageConfig) {
        if (map == null) {
            throw new IllegalArgumentException("map cannot be null");
        }
        this.map = map;
        this.kitchenLoop = kitchenLoop;
        this.orderManager = orderManager;
        this.scoreManager = scoreManager;
        this.stageConfig = stageConfig;

        // Set sisa waktu berdasarkan durasi di config
        if (stageConfig != null) {
            this.remainingTimeMs = stageConfig.durationSeconds * 1000L;
        }

        scanStations();
    }

    // Mencari semua station di dalam map, lalu dimasukkan ke list masing-masing tipe.
    private void scanStations() {
        int width = map.getWidth();
        int height = map.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = map.getTileAt(x, y);
                if (tile == null || !tile.hasStation()) continue;

                Station s = tile.getStation();
                if (s instanceof CuttingStation) {
                    cuttingStations.add((CuttingStation) s);
                } else if (s instanceof CookingStation) {
                    cookingStations.add((CookingStation) s);
                } else if (s instanceof WashingStation) {
                    washingStations.add((WashingStation) s);
                }
            }
        }

        System.out.println("GameLoop scan:");
        System.out.println("  CuttingStations : " + cuttingStations.size());
        System.out.println("  CookingStations : " + cookingStations.size());
        System.out.println("  WashingStations : " + washingStations.size());
    }

    // Fungsi utama yang dipanggil setiap tick
    // deltaTimeMs selisih waktu sejak update terakhir
    public void update(int deltaTime) {
        if (deltaTime <= 0) return;
        if (stageOver) return; 

        // ===UPDATE TIMER STAGE ===
        if (stageConfig != null && remainingTimeMs > 0) {
            remainingTimeMs -= deltaTime;
            if (remainingTimeMs < 0) remainingTimeMs = 0;
        }

        // === UPDATE STATION & KITCHEN LOOP ===
        for (CuttingStation cs : cuttingStations) {
            cs.update(deltaTime);
        }

        for (CookingStation cs : cookingStations) {
            cs.update(deltaTime);
        }

        for (WashingStation ws : washingStations) {
            ws.update(deltaTime);
        }

        if (kitchenLoop != null) {
            kitchenLoop.update(deltaTime);
        }

        if (orderManager != null) {
            // Hapus order yang sudah kadaluarsa dan ambil total penalty
            int totalPenalty = orderManager.purgeExpired();
            if (totalPenalty > 0 && scoreManager != null) {
                scoreManager.subtractScore(totalPenalty);
            }

            // === CEK FAILED-STREAK ===
            if (stageConfig != null){
                int streak = orderManager.getFailedStreak();
                if (streak >= stageConfig.maxFailedOrders) {
                    stageOver = true;
                    stagePass = false;
                    failedByTooManyOrders = true;
                    return;
                }
            }
        }

        // === CEK TIME'S UP ===
        if (stageConfig != null && remainingTimeMs == 0) {
            int currentScore = (scoreManager != null) ? scoreManager.getScore() : 0;
            stagePass = currentScore >= stageConfig.targetScore;
            stageOver = true;
        }
    }

    public boolean isStagePass() {
        return stagePass;
    }

    public int getRemainingTimeSeconds() {
        return (int) Math.ceil(remainingTimeMs / 1000.0);
    }

    public StageConfig getStageConfig() {
        return stageConfig;
    }

    public boolean isFailedByTooManyOrders() {
        return stageOver && !stagePass && failedByTooManyOrders;
    }
    
    // === HUD helper ===
    public String getStageName() {
        return (stageConfig != null) ? stageConfig.getName() : "";
    }

    // Sisa waktu stage dalam detik
    public int getRemainingStageSeconds() {
        if (stageConfig == null) return 0;
        return Math.max(0,(int) remainingTimeMs / 1000);
    }

    public boolean isStageOver() {
        return stageOver;
    }

    public boolean isStageCleared() {
        return stageOver && stagePass;
    }

}
