package src.Game;

import java.awt.*;
import javax.swing.*;
import src.Station.Station;
import src.chef.Chef;
import src.chef.Position;


class MapPanel extends JPanel {
    private final GameMap map;
    private Chef chef;

    public MapPanel(GameMap map, Chef chef) {
        this.map = map;
        this.chef = chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int cols = map.getWidth();
        int rows = map.getHeight();

        int cellSize = Math.min(getWidth() / cols, getHeight() / rows);
        int xOffset = (getWidth() - cols * cellSize) / 2;
        int yOffset = (getHeight() - rows * cellSize) / 2;

        // gambar tile
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Tile tile = map.getTileAt(x, y);

                int px = xOffset + x * cellSize;
                int py = yOffset + y * cellSize;

                // warna dasar
                if (tile.getType() == TileType.WALL) {
                    g.setColor(Color.DARK_GRAY);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillRect(px, py, cellSize, cellSize);

                // border
                g.setColor(Color.BLACK);
                g.drawRect(px, py, cellSize, cellSize);

                // station digambar kotak oranye + huruf
                if (tile.hasStation()) {
                    Station s = tile.getStation();
                    g.setColor(Color.ORANGE);
                    g.fillRect(px + cellSize / 4, py + cellSize / 4,
                               cellSize / 2, cellSize / 2);

                    g.setColor(Color.BLACK);
                    String label = String.valueOf(s.getType().getSymbol());

                    // pakai FontMetrics untuk center text
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = fm.stringWidth(label);
                    int textHeight = fm.getAscent();
                    g.drawString(label,
                        px + (cellSize - textWidth) / 2,
                        py + (cellSize + textHeight) / 2 - 2);
                }
            }
        }

        // gambar Chef
        Position cPos = chef.getPosition();
        if (cPos != null) {
            int cx = xOffset + cPos.getX() * cellSize;
            int cy = yOffset + cPos.getY() * cellSize;

            g.setColor(Color.BLUE);
            g.fillOval(cx + cellSize / 4, cy + cellSize / 4,
                       cellSize / 2, cellSize / 2);
        }
    }
}
