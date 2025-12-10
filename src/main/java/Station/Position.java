package Station;

import Game.Direction;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;

        Position p = (Position) o;
        return this.x == p.x && this.y == p.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
    public Position move(Direction dir) {
    int nx = this.x;
    int ny = this.y;

    switch (dir) {
        case UP:    ny--; break;
        case DOWN:  ny++; break;
        case LEFT:  nx--; break;
        case RIGHT: nx++; break;
    }

    return new Position(nx, ny);
}


}
