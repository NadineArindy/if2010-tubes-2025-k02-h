package src.Game;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import src.chef.Chef;
import src.chef.Position;
import src.chef.Direction;

public class Main {
    public static void main(String[] args) {
        // Buat map dari factory
        GameMap map = MapFactory.createSampleMap();

        // Spawn dua chef
        Position spawn = map.getSpawnPoint();
        Chef chefA = new Chef("C1", "Riko", spawn);
        Chef chefB = new Chef("C2", "Partner", new Position(spawn.getX() + 1, spawn.getY()));

        // Controller untuk switch chef
        GameController controller = new GameController(chefA, chefB);

        // Setup JFrame
        JFrame frame = new JFrame("Nimonscooked Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel map, kirim semua chef + chef aktif
        Chef[] allChefs = new Chef[]{chefA, chefB};
        MapPanel panel = new MapPanel(map, allChefs, controller.getActiveChef());
        frame.add(panel);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Keyboard input
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Chef active = controller.getActiveChef();
                Chef[] others = new Chef[]{controller.getInactiveChef()};

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_TAB: // switch chef
                        controller.switchChef();
                        panel.setActiveChef(controller.getActiveChef()); // update panel
                        break;
                    case KeyEvent.VK_W:
                        active.move(Direction.UP, map, others);
                        break;
                    case KeyEvent.VK_S:
                        active.move(Direction.DOWN, map, others);
                        break;
                    case KeyEvent.VK_A:
                        active.move(Direction.LEFT, map, others);
                        break;
                    case KeyEvent.VK_D:
                        active.move(Direction.RIGHT, map, others);
                        break;
                    case KeyEvent.VK_E: // tombol aksi interaksi
                        active.interact(map);
                        break;
                }
                panel.repaint();
            }
        });

        // Timer untuk repaint otomatis
        new Timer(100, e -> panel.repaint()).start();
    }
}
