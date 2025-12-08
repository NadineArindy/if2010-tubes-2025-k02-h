package src.Station;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import src.Game.ScoreManager;
import src.Item.Plate;

public class KitchenLoop {
    public static final int PLATE_RETURN_TIME = 10_000;
    private final PlateStorage plateStorage;
    private final List<ReturningPlate> returningPlates = new ArrayList<>();
    private final ScoreManager scoreManager;

    public KitchenLoop(PlateStorage plateStorage, ScoreManager scoreManager) {
        if(plateStorage == null){
            throw new IllegalArgumentException("PlateStorage cannot be null");
        }
        this.plateStorage = plateStorage;
        this.scoreManager = scoreManager;
    }

    public void schedulePlateReturn(Plate plate) {
        if (plate == null) {
            return;
        }
        returningPlates.add(new ReturningPlate(plate, PLATE_RETURN_TIME));
    }

    public void update(int deltaTime) {
        if(deltaTime <= 0){
            return;
        }

        Iterator<ReturningPlate> iterator = returningPlates.iterator();
        while (iterator.hasNext()) {
            ReturningPlate returningPlate = iterator.next();
            returningPlate.tick(deltaTime);
            if (returningPlate.isReady()) {
                Plate plate = returningPlate.getPlate();

                // default: plate kembali dalam kondisi dirty
                plate.setClean(false);
                plateStorage.addPlate(plate);

                // Integrasi ScoreManager di sini
                if(plate.isClean()){
                    scoreManager.addScore(2); // bonus kalau plate kembali bersih
                } else {
                    scoreManager.subtractScore(1); // penalti kalau dirty
                }

                iterator.remove();
            }
        }
    }
    
    private static class ReturningPlate {
        private final Plate plate;
        private int remainingTime;

        ReturningPlate(Plate plate, int remainingTime) {
            this.plate = plate;
            this.remainingTime = remainingTime;
        }

        Plate getPlate() {
            return plate;
        }

        void tick(int time) {
            remainingTime -= time;
        }

        boolean isReady() {
            return remainingTime <= 0;
        }
    }  
}