package src;
// Main.java
// Demo sederhana Overcooked-like: map 14x10 + Chef bisa jalan pakai arrow keys

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMap map = MapFactory.createSampleMap();  // bikin map dari layout char
            Chef chef = new Chef("1", "Nadine", map.getSpawnPoint());

            GameFrame frame = new GameFrame(map, chef);
            frame.setVisible(true);
        });
    }
}

// ================= GUI =================

