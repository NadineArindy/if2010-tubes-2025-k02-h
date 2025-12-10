
package src;
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
