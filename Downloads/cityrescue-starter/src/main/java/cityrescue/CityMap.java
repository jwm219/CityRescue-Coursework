package cityrescue;

//represents grid of the city and stores obstacle locations
public class CityMap {
    private int width;
    private int height;
    private boolean[][] blocked;

    //creates a map of a given size
    public CityMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.blocked = new boolean[width][height];
    }

    //check that coordinates are within grid
    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    //obstacles in the way?
    public boolean isBlocked(int x, int y) { return blocked[x][y]; }
    public void setBlocked(int x, int y, boolean value) { blocked[x][y] = value; }
   
    //getters
    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    //calculates distance between points
    public int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    //counts number of obstacles
    public int countObstacles() {
        int count = 0;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (blocked[x][y]) count++;
        return count;
    }
}
