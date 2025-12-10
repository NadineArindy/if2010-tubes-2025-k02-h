package src;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KitchenLoop {
    public static final int PLATE_RETURN_TIME = 10_000;
    private final PlateStorage plateStorage;
    private final List<ReturningPlate> returningPlates = new ArrayList<>();

    public KitchenLoop(PlateStorage plateStorage) {
        if(plateStorage == null){
            throw new IllegalArgumentException("PlateStorage cannot be null");
        }
        this.plateStorage = plateStorage;
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
                plate.setClean(false);
                plateStorage.addPlate(plate);
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