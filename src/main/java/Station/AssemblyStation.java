package Station;

public class AssemblyStation extends Workstation {
    public AssemblyStation(String id, Position position, char symbol, int capacity, int processTime){
        super(id, position, symbol, capacity, processTime);
    }

    @Override
    public void interact(Chef chef) {
        // Use generic logic from Workstation (includes Plating Cases 1-3)
        super.interact(chef);
    }
    
    @Override
        public void startProcess() {
        // Implementasi spesifik di subclass
    }

    @Override
    public void finishProcess() {
        // Implementasi spesifik di subclass
    }
}
