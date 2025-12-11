package src.Station;

import java.util.LinkedList;
import java.util.Queue;

import src.Game.GameContext;
import src.Game.StationType;
import src.Item.Item;
import src.Item.Plate;
import src.chef.Chef;
import src.chef.Position;

public class WashingStation extends Station {
    private final Queue<Plate> dirtyPlates;
    private final Queue<Plate> cleanPlates;
    private Plate currentPlate;
    private boolean isWashing;
    private int remainingWashTime;
    public static final int WASH_TIME = 3000; // Waktu mencuci dalam satuan tick
    private Chef workingChef;

    public WashingStation(String id, Position position, char symbol, StationType type) {
        super(id, position, symbol, type);
        this.dirtyPlates = new LinkedList<>();
        this.cleanPlates = new LinkedList<>();
        this.currentPlate = null;
        this.isWashing = false;
        this.remainingWashTime = 0;
        this.workingChef = null;
    }

    public boolean hasDirtyPlates() {
        return currentPlate != null || !dirtyPlates.isEmpty();
    }

    public boolean hasCleanPlates() {
        return !cleanPlates.isEmpty();
    }

    public boolean isWashing() {
        return isWashing;
    }

    // Menambahkan piring kotor ke queue dirtyPlates
    public void addDirtyPlate(Plate plate) {
        if (plate == null || plate.isClean()) {
            return;
        }
        dirtyPlates.offer(plate);
        GameContext.getMessenger().info("Piring kotor dimasukkan ke antrian cuci.");
    }

    // Ambil satu piring bersih dari antrian
    public Plate takeCleanPlate() {
        return cleanPlates.poll();
    }

    public void startWashing(Chef chef) {
        if (isWashing || dirtyPlates.isEmpty()) {
            return;
        }

        // Jika belum ada piring yang dicuci, ambil dari antrian
        if(currentPlate == null){
            if(dirtyPlates.isEmpty()){
                return;
            }
            
            currentPlate = dirtyPlates.poll();
            
            // Set waktu cuci
            if(remainingWashTime <= 0){
                remainingWashTime = WASH_TIME;
            }
        }

        isWashing = true;
        workingChef = chef;
        chef.startBusy();       // Menandai chef sedang sibuk mencuci
        chef.setCurrentStation(this);

        GameContext.getMessenger().info("Mulai mencuci satu piring...");
    }

    // Untuk mengurangi sisa waktu mencuci
    public void update(int deltaTime) {
        if(!isWashing || currentPlate == null){
            return;
        }

        remainingWashTime -= deltaTime;
        if(remainingWashTime <= 0){
            finishWashing();
        }
    }

    // Berhenti sementara ketika chef meninggalkan station
    public void pauseWashing() {
        isWashing = false;
        if (workingChef != null) {
            workingChef.cancelCurrentAction();
            workingChef.stopBusy();
            workingChef.setCurrentStation(null);
        }

        GameContext.getMessenger().info("Proses mencuci dijeda.");
    }

    public void finishWashing() {
        if (currentPlate == null) {
            return;
        }

        // Menandai piring jadi bersih dan pindah ke antrian piring bersih
        currentPlate.setClean(true);
        cleanPlates.offer(currentPlate);

        GameContext.getMessenger().info("Satu piring sudah bersih dan siap dipakai lagi.");

        //Reset status cuci
        currentPlate = null;
        isWashing = false;
        remainingWashTime = 0;

        //Chef sudah tidak sibuk
        if(workingChef != null){
            workingChef.stopBusy();
            workingChef.setCurrentStation(null);
            workingChef = null;
        }
    }

    @Override
    public void onChefLeave(Chef chef) {
        if (chef == null) {
            return;
        }

        // Jika chef yang meninggalkan station adalah chef yang sedang mencuci
        // maka proses berhenti sementara
        if (chef == workingChef && isWashing) {
            pauseWashing();
        }
    }

    @Override
    public float getProgress() {
        // Jika tidak ada piring yang sedang dicuci, tidak ada progress
        if (currentPlate == null) {
            return -1f;
        }

        if (remainingWashTime <= 0 || remainingWashTime > WASH_TIME) {
            return -1f;
        }

        // Hitung berapa persen progress mencuci yang sudah selesai (0.0 - 0.1)
        float done = WASH_TIME - remainingWashTime;
        return done / (float) WASH_TIME;
    }


    @Override
    public void interact(Chef chef) {
        if(chef == null){
            return;
        }

        Item inHand = chef.getInventory();

        // Jika chef memegang piring kotor, letakkan di antrian cuci
        if(inHand instanceof Plate && !((Plate) inHand).isClean()){
            Plate plate = (Plate) inHand;
            addDirtyPlate(plate);
            chef.setInventory(null);
            return;
        }
        
        // Jika chef tidak memegang apa-apa dan ada piring bersih, ambil piring bersih
        if(inHand == null && hasCleanPlates()){
            Plate cleanPlate = takeCleanPlate();
            if(cleanPlate != null){
                chef.setInventory(cleanPlate);
                GameContext.getMessenger().info("Chef mengambil satu piring bersih dari WashingStation.");
            }
            return;
        }

        // Jika chef tidak memegang apa-apa dan ada piring kotor, mulai mencuci
        if (inHand == null && !isWashing && hasDirtyPlates()) {
            startWashingAsync(chef);
            return;
        }
    }

    private void startWashingAsync(Chef chef) {
        if (currentPlate == null && dirtyPlates.isEmpty()) {
            return;
        }

        if (currentPlate == null) {
            currentPlate = dirtyPlates.poll();
        }

        if (remainingWashTime <= 0) {
            remainingWashTime = WASH_TIME;
        }

        isWashing = true;
        workingChef = chef;

        chef.setCurrentStation(this);
        chef.startAsyncAction(() -> {
            long last = System.currentTimeMillis();

            while (!chef.isActionCancelled() && remainingWashTime > 0) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }

                long now = System.currentTimeMillis();
                int dt = (int) (now - last);
                last = now;

                remainingWashTime -= dt;
            }

            isWashing = false;

            if (!chef.isActionCancelled() && remainingWashTime <= 0) {
                finishWashing();
            }
        });
    }

}
