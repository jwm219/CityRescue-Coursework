package cityrescue;

//represents rescue station that holds units
public class Station {
    private int stationId;
    private String name;
    private int x;
    private int y;
    private int maxCapacity;
    private int currentUnitCount;

    //creates a station. default capacity unlimted
    public Station(int stationId, String name, int x, int y) {
        this.stationId = stationId;
        this.name = name;
        this.x = x;
        this.y = y;
        this.maxCapacity = Integer.MAX_VALUE;
        this.currentUnitCount = 0;
    }

    //checks if station can accept more units
    public boolean hasCapacity() { return currentUnitCount < maxCapacity; }

    //updates number of units at station
    public void incrementUnitCount() { currentUnitCount++; }
    public void decrementUnitCount() { currentUnitCount--; }

    //getters and setters
    public int    getStationId()        { return stationId; }
    public String getName()             { return name; }
    public int    getX()                { return x; }
    public int    getY()                { return y; }
    public int    getMaxCapacity()      { return maxCapacity; }
    public int    getCurrentUnitCount() { return currentUnitCount; }
    public void   setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
}
