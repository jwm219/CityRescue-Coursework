package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

//this file is a full implmentation of the city rescue interface it manages the city map, stations, units, incidents and ticks of the simulation
public class CityRescueImpl implements CityRescue {

    //system limits
    private static final int MAX_STATIONS  = 20;
    private static final int MAX_UNITS     = 50;
    private static final int MAX_INCIDENTS = 200;

    //stores core data
    private CityMap    cityMap;
    private Station[]  stations;
    private Unit[]     units;
    private Incident[] incidents;

    //counters
    private int stationCount;
    private int unitCount;
    private int incidentCount;

    //id trackers
    private int nextStationId;
    private int nextUnitId;
    private int nextIncidentId;
    private int currentTick;

    //resets simulation
    @Override
    public void initialise(int width, int height) throws InvalidGridException {
        if (width <= 0 || height <= 0)
            throw new InvalidGridException("Width and height must be > 0");
        cityMap        = new CityMap(width, height);
        stations       = new Station[MAX_STATIONS];
        units          = new Unit[MAX_UNITS];
        incidents      = new Incident[MAX_INCIDENTS];
        stationCount   = 0;
        unitCount      = 0;
        incidentCount  = 0;
        nextStationId  = 1;
        nextUnitId     = 1;
        nextIncidentId = 1;
        currentTick    = 0;
    }

    //return grid size
    @Override
    public int[] getGridSize() {
        return new int[]{cityMap.getWidth(), cityMap.getHeight()};
    }

    //addblocked locations on map
    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException {
        if (!cityMap.isInBounds(x, y))
            throw new InvalidLocationException("Location out of bounds");
        cityMap.setBlocked(x, y, true);
    }

    //remove blocked locations on map
    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException {
        if (!cityMap.isInBounds(x, y))
            throw new InvalidLocationException("Location out of bounds");
        cityMap.setBlocked(x, y, false);
    }

    //create and store new station
    @Override
    public int addStation(String name, int x, int y) throws InvalidNameException, InvalidLocationException {
        if (name == null || name.trim().isEmpty())
            throw new InvalidNameException("Station name must not be blank");
        if (!cityMap.isInBounds(x, y))
            throw new InvalidLocationException("Location out of bounds");
        if (cityMap.isBlocked(x, y))
            throw new InvalidLocationException("Location is blocked");
        if (stationCount >= MAX_STATIONS)
            throw new InvalidLocationException("Max stations reached");
        Station station = new Station(nextStationId++, name, x, y);
        stations[stationCount++] = station;
        return station.getStationId();
    }

    //removes empty stations
    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException {
        int idx = findStationIndex(stationId);
        if (idx == -1) throw new IDNotRecognisedException("Station not found");
        if (stations[idx].getCurrentUnitCount() > 0)
            throw new IllegalStateException("Station still has units");
        for (int i = idx; i < stationCount - 1; i++)
            stations[i] = stations[i + 1];
        stations[--stationCount] = null;
    }

    //updates station capacity
    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException {
        int idx = findStationIndex(stationId);
        if (idx == -1) throw new IDNotRecognisedException("Station not found");
        if (maxUnits <= 0 || maxUnits < stations[idx].getCurrentUnitCount())
            throw new InvalidCapacityException("Invalid capacity value");
        stations[idx].setMaxCapacity(maxUnits);
    }

    //return sorted stationID
    @Override
    public int[] getStationIds() {
        int[] ids = new int[stationCount];
        for (int i = 0; i < stationCount; i++)
            ids[i] = stations[i].getStationId();
        sortAscending(ids);
        return ids;
    }

    //add new station unit
    @Override
    public int addUnit(int stationId, UnitType type) throws IDNotRecognisedException, InvalidUnitException, IllegalStateException {
        int sIdx = findStationIndex(stationId);
        if (sIdx == -1)   throw new IDNotRecognisedException("Station not found");
        if (type == null) throw new InvalidUnitException("Unit type must not be null");
        if (!stations[sIdx].hasCapacity())
            throw new IllegalStateException("Station at full capacity");
        if (unitCount >= MAX_UNITS)
            throw new IllegalStateException("Max units reached");
        Station s = stations[sIdx];
        Unit unit = createUnit(nextUnitId++, type, s.getStationId(), s.getX(), s.getY());
        units[unitCount++] = unit;
        s.incrementUnitCount();
        return unit.getUnitId();
    }

    //remove inactive units
    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException {
        int uIdx = findUnitIndex(unitId);
        if (uIdx == -1) throw new IDNotRecognisedException("Unit not found");
        Unit unit = units[uIdx];
        if (unit.getStatus() == UnitStatus.EN_ROUTE || unit.getStatus() == UnitStatus.AT_SCENE)
            throw new IllegalStateException("Cannot decommission active unit");
        int sIdx = findStationIndex(unit.getHomeStationId());
        if (sIdx != -1) stations[sIdx].decrementUnitCount();
        for (int i = uIdx; i < unitCount - 1; i++)
            units[i] = units[i + 1];
        units[--unitCount] = null;
    }

    //tranfser idle units to new station
    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException {
        int uIdx = findUnitIndex(unitId);
        if (uIdx == -1) throw new IDNotRecognisedException("Unit not found");
        int sIdx = findStationIndex(newStationId);
        if (sIdx == -1) throw new IDNotRecognisedException("New station not found");
        Unit unit = units[uIdx];
        Station dest = stations[sIdx];
        if (unit.getStatus() != UnitStatus.IDLE)
            throw new IllegalStateException("Unit must be IDLE to transfer");
        if (!dest.hasCapacity())
            throw new IllegalStateException("Destination station at full capacity");
        int oldSIdx = findStationIndex(unit.getHomeStationId());
        if (oldSIdx != -1) stations[oldSIdx].decrementUnitCount();
        unit.setHomeStationId(dest.getStationId());
        unit.setCurrentX(dest.getX());
        unit.setCurrentY(dest.getY());
        dest.incrementUnitCount();
    }

    //mark unit as in/out of service
    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException {
        int uIdx = findUnitIndex(unitId);
        if (uIdx == -1) throw new IDNotRecognisedException("Unit not found");
        Unit unit = units[uIdx];
        if (outOfService && unit.getStatus() != UnitStatus.IDLE)
            throw new IllegalStateException("Unit must be IDLE to set out of service");
        unit.setStatus(outOfService ? UnitStatus.OUT_OF_SERVICE : UnitStatus.IDLE);
    }

    //return sorted unit ID
    @Override
    public int[] getUnitIds() {
        int[] ids = new int[unitCount];
        for (int i = 0; i < unitCount; i++)
            ids[i] = units[i].getUnitId();
        sortAscending(ids);
        return ids;
    }

    //return unit details
    @Override
    public String viewUnit(int unitId) throws IDNotRecognisedException {
        int uIdx = findUnitIndex(unitId);
        if (uIdx == -1) throw new IDNotRecognisedException("Unit not found");
        return formatUnit(units[uIdx]);
    }

    //reports new incident
    @Override
    public int reportIncident(IncidentType type, int severity, int x, int y) throws InvalidSeverityException, InvalidLocationException {
        if (type == null)
            throw new InvalidLocationException("Incident type must not be null");
        if (severity < 1 || severity > 5)
            throw new InvalidSeverityException("Severity must be between 1 and 5");
        if (!cityMap.isInBounds(x, y))
            throw new InvalidLocationException("Location out of bounds");
        if (cityMap.isBlocked(x, y))
            throw new InvalidLocationException("Location is blocked");
        if (incidentCount >= MAX_INCIDENTS)
            throw new InvalidLocationException("Max incidents reached");
        Incident incident = new Incident(nextIncidentId++, type, severity, x, y);
        incidents[incidentCount++] = incident;
        return incident.getIncidentId();
    }

    //cancels incident. assigned unit now free
    @Override
    public void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException {
        int iIdx = findIncidentIndex(incidentId);
        if (iIdx == -1) throw new IDNotRecognisedException("Incident not found");
        Incident incident = incidents[iIdx];
        if (incident.getStatus() != IncidentStatus.REPORTED
                && incident.getStatus() != IncidentStatus.DISPATCHED)
            throw new IllegalStateException("Can only cancel REPORTED or DISPATCHED incidents");
        if (incident.getStatus() == IncidentStatus.DISPATCHED) {
            int uIdx = findUnitIndex(incident.getAssignedUnitId());
            if (uIdx != -1) {
                units[uIdx].setStatus(UnitStatus.IDLE);
                units[uIdx].setAssignedIncidentId(-1);
            }
        }
        incident.setStatus(IncidentStatus.CANCELLED);
        incident.setAssignedUnitId(-1);
    }

    //change severity of active incident
    @Override
    public void escalateIncident(int incidentId, int newSeverity) throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException {
        int iIdx = findIncidentIndex(incidentId);
        if (iIdx == -1) throw new IDNotRecognisedException("Incident not found");
        if (newSeverity < 1 || newSeverity > 5)
            throw new InvalidSeverityException("Severity must be between 1 and 5");
        if (incidents[iIdx].getStatus() == IncidentStatus.RESOLVED
                || incidents[iIdx].getStatus() == IncidentStatus.CANCELLED)
            throw new IllegalStateException("Cannot escalate RESOLVED or CANCELLED incident");
        incidents[iIdx].setSeverity(newSeverity);
    }

    //returns sorted incident ID
    @Override
    public int[] getIncidentIds() {
        int[] ids = new int[incidentCount];
        for (int i = 0; i < incidentCount; i++)
            ids[i] = incidents[i].getIncidentId();
        sortAscending(ids);
        return ids;
    }

    //returns formated incident details
    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException {
        int iIdx = findIncidentIndex(incidentId);
        if (iIdx == -1) throw new IDNotRecognisedException("Incident not found");
        return formatIncident(incidents[iIdx]);
    }

    //dispatches best available unit
    @Override
    public void dispatch() {
        int[] sortedIncidentIndices = getSortedIncidentIndices();
        for (int i = 0; i < incidentCount; i++) {
            Incident incident = incidents[sortedIncidentIndices[i]];
            if (incident.getStatus() != IncidentStatus.REPORTED) continue;
            Unit bestUnit = findBestUnit(incident);
            if (bestUnit == null) continue;
            bestUnit.setStatus(UnitStatus.EN_ROUTE);
            bestUnit.setAssignedIncidentId(incident.getIncidentId());
            incident.setStatus(IncidentStatus.DISPATCHED);
            incident.setAssignedUnitId(bestUnit.getUnitId());
        }
    }

    //advances sim by 1 tick
    @Override
    public void tick() {
        currentTick++;
        int[] sortedUnitIndices     = getSortedUnitIndices();
        int[] sortedIncidentIndices = getSortedIncidentIndices();

        //moves units towards their targets
        for (int i = 0; i < unitCount; i++) {
            Unit unit = units[sortedUnitIndices[i]];
            if (unit.getStatus() != UnitStatus.EN_ROUTE) continue;
            Incident target = incidents[findIncidentIndex(unit.getAssignedIncidentId())];
            unit.moveTowards(target.getX(), target.getY(), cityMap);
        }

        //checks if units arrived and updates stautuses
        for (int i = 0; i < unitCount; i++) {
            Unit unit = units[sortedUnitIndices[i]];
            if (unit.getStatus() != UnitStatus.EN_ROUTE) continue;
            Incident target = incidents[findIncidentIndex(unit.getAssignedIncidentId())];
            if (unit.getCurrentX() == target.getX() && unit.getCurrentY() == target.getY()) {
                unit.setStatus(UnitStatus.AT_SCENE);
                unit.setTicksWorkedAtScene(0);
                target.setStatus(IncidentStatus.IN_PROGRESS);
            }
        }

        //adds work time for units at a scene
        for (int i = 0; i < unitCount; i++) {
            Unit unit = units[sortedUnitIndices[i]];
            if (unit.getStatus() == UnitStatus.AT_SCENE)
                unit.setTicksWorkedAtScene(unit.getTicksWorkedAtScene() + 1);
        }

        //resolve completed incidents by ascending incidentId
        for (int i = 0; i < incidentCount; i++) {
            Incident incident = incidents[sortedIncidentIndices[i]];
            if (incident.getStatus() != IncidentStatus.IN_PROGRESS) continue;
            int uIdx = findUnitIndex(incident.getAssignedUnitId());
            if (uIdx == -1) continue;
            Unit unit = units[uIdx];
            if (unit.getTicksWorkedAtScene() >= unit.getTicksToResolve()) {
                incident.setStatus(IncidentStatus.RESOLVED);
                unit.setStatus(UnitStatus.IDLE);
                unit.setAssignedIncidentId(-1);
                unit.setTicksWorkedAtScene(0);
            }
        }
    }

            //system summary
            @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("TICK=").append(currentTick)
          .append(" STATIONS=").append(stationCount)
          .append(" UNITS=").append(unitCount)
          .append(" INCIDENTS=").append(incidentCount)
          .append(" OBSTACLES=").append(cityMap == null ? 0 : cityMap.countObstacles())
          .append("\n");
        sb.append("INCIDENTS\n");
        int[] sortedIIdx = getSortedIncidentIndices();
        for (int i = 0; i < incidentCount; i++)
            sb.append(formatIncident(incidents[sortedIIdx[i]])).append("\n");
        sb.append("UNITS\n");
        int[] sortedUIdx = getSortedUnitIndices();
        for (int i = 0; i < unitCount; i++)
            sb.append(formatUnit(units[sortedUIdx[i]])).append("\n");
        return sb.toString();
    }

            //format incident to readable string
             private String formatIncident(Incident incident) {
        String unitStr = incident.getAssignedUnitId() == -1 ? "-"
                       : String.valueOf(incident.getAssignedUnitId());
        String typeStr = incident.getType().toString().replace("_", "");
        String statusStr = incident.getStatus().toString().replace("_", "");
        return String.format("I#%d TYPE=%s SEV=%d LOC=(%d,%d) STATUS=%s UNIT=%s",
            incident.getIncidentId(), typeStr, incident.getSeverity(),
            incident.getX(), incident.getY(), statusStr, unitStr);
    }

    //format unit to readable string
    private String formatUnit(Unit unit) {
        String incidentStr = unit.getAssignedIncidentId() == -1 ? "-"
                           : String.valueOf(unit.getAssignedIncidentId());
        String typeStr = unit.getType().toString().replace("_", "");
        String statusStr = unit.getStatus().toString().replace("_", "");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("U#%d TYPE=%s HOME=%d LOC=(%d,%d) STATUS=%s INCIDENT=%s",
            unit.getUnitId(), typeStr, unit.getHomeStationId(),
            unit.getCurrentX(), unit.getCurrentY(), statusStr, incidentStr));
        if (unit.getStatus() == UnitStatus.AT_SCENE)
            sb.append(" WORK=").append(unit.getTicksWorkedAtScene());
        return sb.toString();
    }


    //find station index by id
    private int findStationIndex(int stationId) {
        for (int i = 0; i < stationCount; i++)
            if (stations[i].getStationId() == stationId) return i;
        return -1;
    }

    //find unit index by id
    private int findUnitIndex(int unitId) {
        for (int i = 0; i < unitCount; i++)
            if (units[i].getUnitId() == unitId) return i;
        return -1;
    }

    //find incident index by id
    private int findIncidentIndex(int incidentId) {
        for (int i = 0; i < incidentCount; i++)
            if (incidents[i].getIncidentId() == incidentId) return i;
        return -1;
    }

    //returns unit indices sorted by id
    private int[] getSortedUnitIndices() {
        int[] indices = new int[unitCount];
        for (int i = 0; i < unitCount; i++) indices[i] = i;
        for (int i = 1; i < unitCount; i++) {
            int key = indices[i]; int j = i - 1;
            while (j >= 0 && units[indices[j]].getUnitId() > units[key].getUnitId()) {
                indices[j + 1] = indices[j]; j--;
            }
            indices[j + 1] = key;
        }
        return indices;
    }

    //returns incident indices sortedd by id
    private int[] getSortedIncidentIndices() {
        int[] indices = new int[incidentCount];
        for (int i = 0; i < incidentCount; i++) indices[i] = i;
        for (int i = 1; i < incidentCount; i++) {
            int key = indices[i]; int j = i - 1;
            while (j >= 0 && incidents[indices[j]].getIncidentId() > incidents[key].getIncidentId()) {
                indices[j + 1] = indices[j]; j--;
            }
            indices[j + 1] = key;
        }
        return indices;
    }

    //insert sort for array
    private void sortAscending(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i]; int j = i - 1;
            while (j >= 0 && arr[j] > key) { arr[j + 1] = arr[j]; j--; }
            arr[j + 1] = key;
        }
    }

    //finds closest available correct unit for incident
    private Unit findBestUnit(Incident incident) {
        Unit bestUnit = null;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < unitCount; i++) {
            Unit u = units[i];
            if (u.getStatus() != UnitStatus.IDLE) continue;
            if (!u.canHandleIncidentType(incident.getType())) continue;
            int dist = cityMap.manhattanDistance(
                u.getCurrentX(), u.getCurrentY(), incident.getX(), incident.getY());
            if (bestUnit == null
                    || dist < bestDist
                    || (dist == bestDist && u.getUnitId() < bestUnit.getUnitId())
                    || (dist == bestDist && u.getUnitId() == bestUnit.getUnitId()
                        && u.getHomeStationId() < bestUnit.getHomeStationId())) {
                bestUnit = u;
                bestDist = dist;
            }
        }
                return bestUnit;
    }

    //factory method that creates the correct unit type
    private Unit createUnit(int unitId, UnitType type, int homeStationId, int x, int y) {
        switch (type) {
            case AMBULANCE:   return new Ambulance(unitId, homeStationId, x, y);
            case FIRE_ENGINE: return new FireEngine(unitId, homeStationId, x, y);
            case POLICE_CAR:  return new PoliceCar(unitId, homeStationId, x, y);
            default: throw new IllegalArgumentException("Unknown unit type");
        }
    }
}

