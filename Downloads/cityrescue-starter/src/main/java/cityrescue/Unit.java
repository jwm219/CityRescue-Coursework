package cityrescue;

import cityrescue.enums.UnitType;
import cityrescue.enums.UnitStatus;
import cityrescue.enums.IncidentType;

public abstract class Unit {
    protected int unitId;
    protected UnitType type;
    protected int homeStationId;
    protected int currentX;
    protected int currentY;
    protected UnitStatus status;
    protected int assignedIncidentId;
    protected int ticksWorkedAtScene;

    public Unit(int unitId, UnitType type, int homeStationId, int startX, int startY) {
        this.unitId = unitId;
        this.type = type;
        this.homeStationId = homeStationId;
        this.currentX = startX;
        this.currentY = startY;
        this.status = UnitStatus.IDLE;
        this.assignedIncidentId = -1;
        this.ticksWorkedAtScene = 0;
    }

    public abstract boolean canHandleIncidentType(IncidentType incidentType);
    public abstract int getTicksToResolve();

    public void moveTowards(int targetX, int targetY, CityMap map) {
        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};
        int currentDist = map.manhattanDistance(currentX, currentY, targetX, targetY);

        for (int i = 0; i < 4; i++) {
            int newX = currentX + dx[i];
            int newY = currentY + dy[i];
            if (map.isInBounds(newX, newY) && !map.isBlocked(newX, newY)) {
                if (map.manhattanDistance(newX, newY, targetX, targetY) < currentDist) {
                    currentX = newX; currentY = newY; return;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            int newX = currentX + dx[i];
            int newY = currentY + dy[i];
            if (map.isInBounds(newX, newY) && !map.isBlocked(newX, newY)) {
                currentX = newX; currentY = newY; return;
            }
        }
    }

    public int        getUnitId()                       { return unitId; }
    public UnitType   getType()                         { return type; }
    public int        getHomeStationId()                { return homeStationId; }
    public void       setHomeStationId(int id)          { this.homeStationId = id; }
    public int        getCurrentX()                     { return currentX; }
    public int        getCurrentY()                     { return currentY; }
    public void       setCurrentX(int x)                { this.currentX = x; }
    public void       setCurrentY(int y)                { this.currentY = y; }
    public UnitStatus getStatus()                       { return status; }
    public void       setStatus(UnitStatus s)           { this.status = s; }
    public int        getAssignedIncidentId()           { return assignedIncidentId; }
    public void       setAssignedIncidentId(int id)     { this.assignedIncidentId = id; }
    public int        getTicksWorkedAtScene()           { return ticksWorkedAtScene; }
    public void       setTicksWorkedAtScene(int t)      { this.ticksWorkedAtScene = t; }
}