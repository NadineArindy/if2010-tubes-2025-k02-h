package src.Game;

import src.chef.Chef;

public class GameController {
    private final Chef[] chefs;
    private int active = 0;

    public GameController(Chef c1, Chef c2) {
        this.chefs = new Chef[]{c1, c2};
        
    }

    public Chef getActiveChef() { return chefs[active]; }
    public Chef getInactiveChef() { return chefs[1 - active]; }

    public void switchChef() {
        active = 1 - active;
        System.out.println("Switch to " + getActiveChef().getName());
    }
    

}
