package src;
class MapPanel extends JPanel {
    private final GameMap map;
    private final Chef chef;

    public MapPanel(GameMap map, Chef chef) {
        this.map = map;
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
                Tile tile = map.getTileAt(new Position(x, y));

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

                // station (C,R,A,dst) digambar kotak oranye + huruf
                if (tile.hasStation()) {
                    Station s = tile.getStation();
                    g.setColor(Color.ORANGE);
                    g.fillRect(px + cellSize / 4, py + cellSize / 4,
                               cellSize / 2, cellSize / 2);
                    g.setColor(Color.BLACK);
                    String label = String.valueOf(s.getType().getSymbol());
                    g.drawString(label, px + cellSize / 2 - 3,
                                 py + cellSize / 2 + 4);
                }
            }
        }

        // gambar Chef
        Position cPos = chef.getPosition();
        int cellSize2 = Math.min(getWidth() / cols, getHeight() / rows);
        int xOffset2 = (getWidth() - cols * cellSize2) / 2;
        int yOffset2 = (getHeight() - rows * cellSize2) / 2;

        int cx = xOffset2 + cPos.getX() * cellSize2;
        int cy = yOffset2 + cPos.getY() * cellSize2;

        g.setColor(Color.BLUE);
        g.fillOval(cx + cellSize2 / 4, cy + cellSize2 / 4,
                   cellSize2 / 2, cellSize2 / 2);
    }
}



