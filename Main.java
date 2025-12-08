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

// ============= DOMAIN MODEL (sesuai UML-mu, disederhanakan) =============

class Position {
    private final int x; // kolom (0..13)
    private final int y; // baris (0..9)

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public Position translate(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}

enum Direction {
    UP(0, -1),
    DOWN(0,  1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    final int dx;
    final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}

enum TileType {
    FLOOR,
    WALL,
    STATION
}

// jenis station sesuai legenda
enum StationType {
    CUTTING('C'),
    COOKING('R'),
    ASSEMBLY('A'),
    SERVING('S'),
    WASHING('W'),
    INGREDIENT('I'),
    PLATE('P'),
    TRASH('T');

    private final char symbol;

    StationType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() { return symbol; }

    public static StationType fromSymbol(char c) {
        for (StationType t : values()) {
            if (t.symbol == c) return t;
        }
        return null;
    }
}

class Station {
    private final StationType type;
    private final Position position;

    public Station(StationType type, Position position) {
        this.type = type;
        this.position = position;
    }

    public StationType getType() { return type; }
    public Position getPosition() { return position; }
}

class Tile {
    private final Position position;
    private final TileType type;
    private Station station;   // boleh null
    private final boolean walkable;

    public Tile(Position position, TileType type, boolean walkable) {
        this.position = position;
        this.type = type;
        this.walkable = walkable;
    }

    public Position getPosition() { return position; }
    public TileType getType() { return type; }

    public Station getStation() { return station; }
    public void setStation(Station station) { this.station = station; }

    public boolean hasStation() { return station != null; }
    public boolean isWalkable() { return walkable; }
}

class GameMap {
    private final int width;
    private final int height;
    private final Tile[][] tiles;   // [y][x]
    private Position spawnPoint;    // posisi 'V'

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
    }

    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public Tile getTileAt(Position pos) {
        if (!isInBounds(pos)) return null;
        return tiles[pos.getY()][pos.getX()];
    }

    public boolean isInBounds(Position pos) {
        return pos.getX() >= 0 && pos.getX() < width &&
               pos.getY() >= 0 && pos.getY() < height;
    }

    public boolean isWalkable(Position pos) {
        if (!isInBounds(pos)) return false;
        return tiles[pos.getY()][pos.getX()].isWalkable();
    }

    public Station getStationAt(Position pos) {
        Tile t = getTileAt(pos);
        return t != null ? t.getStation() : null;
    }

    public void setTile(int x, int y, Tile tile) {
        tiles[y][x] = tile;
    }

    public Position getSpawnPoint() { return spawnPoint; }
    public void setSpawnPoint(Position spawnPoint) { this.spawnPoint = spawnPoint; }
}

// bikin GameMap dari layout char 14x10
class MapFactory {
    // CONTOH layout (14 kolom x 10 baris) – ganti dengan layout kelompokmu
    // setiap char: C,R,A,S,W,I,P,T,X,.,+,V sesuai legenda
    private static final String[] RAW_MAP = new String[]{
    "XXXXXXXXXARRRA",   // row 1 (14)
    "ACACACAC......A",  // row 2 (14)
    "A.....A.......A",  // row 3 (14)
    "I...V.A..V...I",   // row 4 (14)
    "I..A..A.......I",  // row 5 (14)
    "I..A..A.......A",  // row 6 (14)
    "S..A..A.......A",  // row 7 (14)
    "S..X..T..WWP.X",   // row 8 (14)
    "X.......X....X",   // row 9 (14)
    "XXXXXXXXXXXXXX"    // row 10 (14)
};



    public static GameMap createSampleMap() {
        int height = RAW_MAP.length;
        int width  = RAW_MAP[0].length();

        GameMap map = new GameMap(width, height);

        for (int y = 0; y < height; y++) {
            String row = RAW_MAP[y];
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                Position pos = new Position(x, y);

                Tile tile;

                if (c == 'X' || c == '+') {           // dinding / border
                    tile = new Tile(pos, TileType.WALL, false);
                } else if (c == '.' || c == 'V') {    // ruang jalan (dan spawn)
                    tile = new Tile(pos, TileType.FLOOR, true);
                } else {                              // station (C,R,A,S,W,I,P,T)
                    tile = new Tile(pos, TileType.STATION, false);
                    StationType st = StationType.fromSymbol(c);
                    if (st != null) {
                        Station station = new Station(st, pos);
                        tile.setStation(station);
                    }
                }

                if (c == 'V') {           // spawn chef
                    map.setSpawnPoint(pos);
                }

                map.setTile(x, y, tile);
            }
        }
        return map;
        
    }

    static {
    for (int i = 0; i < RAW_MAP.length; i++) {
        System.out.println("Row " + i + " length = " + RAW_MAP[i].length() + " : " + RAW_MAP[i]);
    }
}

}

class Chef {
    private final String id;
    private final String name;
    private Position position;
    private Direction direction = Direction.DOWN;

    public Chef(String id, String name, Position position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public Position getPosition() { return position; }

    // versi sederhana: cuma gerak kalau tile tujuan walkable
    public void move(Direction dir, GameMap map) {
        this.direction = dir;
        Position newPos = position.translate(dir.dx, dir.dy);
        if (map.isWalkable(newPos)) {
            this.position = newPos;
        }
    }
}
