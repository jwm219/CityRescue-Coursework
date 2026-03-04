package cityrescue;

public class CityMap {
    private int width;
    private int height;
    private boolean[][] blocked;

    public CityMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.blocked = new boolean[width][height];
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isBlocked(int x, int y) { return blocked[x][y]; }
    public void setBlocked(int x, int y, boolean value) { blocked[x][y] = value; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public int countObstacles() {
        int count = 0;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (blocked[x][y]) count++;
        return count;
    }
}
