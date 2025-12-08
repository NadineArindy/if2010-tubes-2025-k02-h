package src.Station;

import src.Item.Item;
import src.Item.Plate;
import java.util.LinkedList;
import java.util.Queue;

public class WashingStation extends Station {
    private final Queue<Plate> dirtyPlates;
    private final Queue<Plate> cleanPlates;
    private Plate currentPlate;
    private boolean isWashing;
    private int remainingWashTime;
    public static final int WASH_TIME = 3000; // Waktu mencuci dalam satuan tick

    public WashingStation(String id, Position position, char symbol) {
        super(id, position, symbol);
        this.dirtyPlates = new LinkedList<>();
        this.cleanPlates = new LinkedList<>();
        this.currentPlate = null;
        this.isWashing = false;
        this.remainingWashTime = 0;
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

    public void addDirtyPlate(Plate plate) {
        if (plate == null || plate.isClean()) {
            return;
        }
        dirtyPlates.offer(plate);
    }

    public Plate takeCleanPlate() {
        return cleanPlates.poll();
    }

    public void startWashing() {
        if (isWashing || dirtyPlates.isEmpty()) {
            return;
        }

        if(currentPlate == null){
            if(dirtyPlates.isEmpty()){
                return;
            }
            
            currentPlate = dirtyPlates.poll();
            
            if(remainingWashTime <= 0){
                remainingWashTime = WASH_TIME;
            }
        }

        isWashing = true;
    }

    public void update(int deltaTime) {
        if(!isWashing || currentPlate == null){
            return;
        }

        remainingWashTime -= deltaTime;
        if(remainingWashTime <= 0){
            finishWashing();
        }
    }

    public void finishWashing() {
        if (currentPlate == null) {
            return;
        }

        currentPlate.setClean(true);
        cleanPlates.offer(currentPlate);
        currentPlate = null;
        isWashing = false;
        remainingWashTime = 0;
    }

    @Override
    public void interact(Chef chef) {
        if(chef == null){
            return;
        }

        Item inHand = chef.getInventory();
        if(inHand instanceof Plate plate && !plate.isClean()){
            addDirtyPlate(plate);
            chef.setInventory(null);
            return;
        }
        
        if(inHand == null && hasCleanPlates()){
            Plate cleanPlate = takeCleanPlate();
            if(cleanPlate != null){
                chef.setInventory(cleanPlate);
            }
            return;
        }

        if (inHand == null && !isWashing && hasDirtyPlates()) {
            startWashing();
        }
    }
}
