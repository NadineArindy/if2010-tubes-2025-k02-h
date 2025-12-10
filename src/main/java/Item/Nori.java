package Item;

public class Nori extends Ingredient{

    public Nori() {
        super("Nori");
    }

    public Nori(String name) {
        super(name);
    }

    @Override
    public boolean isReady() {
        return true;
    }
    
}
