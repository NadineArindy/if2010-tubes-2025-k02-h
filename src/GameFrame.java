package src;
class GameFrame extends JFrame {
    private final GameMap map;
    private final Chef chef;

    public GameFrame(GameMap map, Chef chef) {
        this.map = map;
        this.chef = chef;

        setTitle("Overcooked Map Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        MapPanel panel = new MapPanel(map, chef);
        setContentPane(panel);

        // pakai KeyBinding biar enak
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap im = panel.getInputMap(condition);
        ActionMap am = panel.getActionMap();

        im.put(KeyStroke.getKeyStroke("UP"),    "moveUp");
        im.put(KeyStroke.getKeyStroke("DOWN"),  "moveDown");
        im.put(KeyStroke.getKeyStroke("LEFT"),  "moveLeft");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        // WASD keys
        im.put(KeyStroke.getKeyStroke('W'), "moveUp");
        im.put(KeyStroke.getKeyStroke('A'), "moveLeft");
        im.put(KeyStroke.getKeyStroke('S'), "moveDown");
        im.put(KeyStroke.getKeyStroke('D'), "moveRight");

        im.put(KeyStroke.getKeyStroke('w'), "moveUp");
        im.put(KeyStroke.getKeyStroke('a'), "moveLeft");
        im.put(KeyStroke.getKeyStroke('s'), "moveDown");
        im.put(KeyStroke.getKeyStroke('d'), "moveRight");


        am.put("moveUp",    new MoveAction(Direction.UP, panel));
        am.put("moveDown",  new MoveAction(Direction.DOWN, panel));
        am.put("moveLeft",  new MoveAction(Direction.LEFT, panel));
        am.put("moveRight", new MoveAction(Direction.RIGHT, panel));
    }

    private class MoveAction extends AbstractAction {
        private final Direction dir;
        private final JComponent comp;

        public MoveAction(Direction dir, JComponent comp) {
            this.dir = dir;
            this.comp = comp;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            chef.move(dir, map);
            comp.repaint();
        }
    }
}

