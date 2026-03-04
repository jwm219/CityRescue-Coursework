package cityrescue;

public class Station {
    private int stationId;
    private String name;
    private int x;
    private int y;
    private int maxCapacity;
    private int currentUnitCount;

    public Station(int stationId, String name, int x, int y) {
        this.stationId = stationId;
        this.name = name;
        this.x = x;
        this.y = y;
        this.maxCapacity = Integer.MAX_VALUE;
        this.currentUnitCount = 0;
    }

    public boolean hasCapacity() { return currentUnitCount < maxCapacity; }
    public void incrementUnitCount() { currentUnitCount++; }
    public void decrementUnitCount() { currentUnitCount--; }

    public int    getStationId()        { return stationId; }
    public String getName()             { return name; }
    public int    getX()                { return x; }
    public int    getY()                { return y; }
    public int    getMaxCapacity()      { return maxCapacity; }
    public int    getCurrentUnitCount() { return currentUnitCount; }
    public void   setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
}
