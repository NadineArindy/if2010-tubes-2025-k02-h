package src.Game;

import src.chef.Position;

public class GameMap {
    private final int width;
    private final int height;
    private final Tile[][] tiles;
    private final boolean[][] walkable;
    private Position spawnPoint;

    public GameMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Map dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
        this.walkable = new boolean[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                walkable[y][x] = true;
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Tile getTileAt(int x, int y) {
        if (!isInside(x, y)) return null;
        return tiles[y][x];
    }

    public void setTile(int x, int y, Tile tile) {
        if (isInside(x, y)) {
            tiles[y][x] = tile;
            walkable[y][x] = tile.isWalkable();
        }
    }

    public void setSpawnPoint(Position pos) {
        this.spawnPoint = pos;
    }

    public Position getSpawnPoint() {
        return spawnPoint;
    }

    public boolean isWalkable(int x, int y) {
        return isInside(x, y) && walkable[y][x];
    }

    public void setObstacle(int x, int y) {
        if (isInside(x, y)) {
            walkable[y][x] = false;
        }
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GameMap:\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(walkable[y][x] ? "." : "#");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
