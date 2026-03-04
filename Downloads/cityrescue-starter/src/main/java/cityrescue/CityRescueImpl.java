package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

/**
 * This file is a full implementation of the CityRescue interface.
 * It manages the city map, stations, units, incidents,also tick of the simulation
 */
public class CityRescueImpl implements CityRescue {

    private static final int MAX_STATIONS  = 20;
    private static final int MAX_UNITS     = 50;
    private static final int MAX_INCIDENTS = 200;

    private CityMap    cityMap;
    private Station[]  stations;
    private Unit[]     units;
    private Incident[] incidents;

    private int stationCount;
    private int unitCount;
    private int incidentCount;

    private int nextStationId;
    private int nextUnitId;
    private int nextIncidentId;
    private int currentTick;

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

    @Override
    public int[] getGridSize() {
        return new int[]{cityMap.getWidth(), cityMap.getHeight()};
    }

    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException {
        if (!cityMap.isInBounds(x, y))
            throw new InvalidLocationException("Location out of bounds");
        cityMap.setBlocked(x, y, true);
    }

    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException {
        if (!cityMap.isInBounds(x, y))
            throw new InvalidLocationException("Location out of bounds");
        cityMap.setBlocked(x, y, false);
    }

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

    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException {
        int idx = findStationIndex(stationId);
        if (idx == -1) throw new IDNotRecognisedException("Station not found");
        if (maxUnits <= 0 || maxUnits < stations[idx].getCurrentUnitCount())
            throw new InvalidCapacityException("Invalid capacity value");
        stations[idx].setMaxCapacity(maxUnits);
    }

    @Override
    public int[] getStationIds() {
        int[] ids = new int[stationCount];
        for (int i = 0; i < stationCount; i++)
            ids[i] = stations[i].getStationId();
        sortAscending(ids);
        return ids;
    }

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

    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException {
        int uIdx = findUnitIndex(unitId);
        if (uIdx == -1) throw new IDNotRecognisedException("Unit not found");
        Unit unit = units[uIdx];
        if (outOfService && unit.getStatus() != UnitStatus.IDLE)
            throw new IllegalStateException("Unit must be IDLE to set out of service");
        unit.setStatus(outOfService ? UnitStatus.OUT_OF_SERVICE : UnitStatus.IDLE);
    }

    @Override
    public int[] getUnitIds() {
        int[] ids = new int[unitCount];
        for (int i = 0; i < unitCount; i++)
            ids[i] = units[i].getUnitId();
        sortAscending(ids);
        return ids;
    }

    @Override
    public String viewUnit(int unitId) throws IDNotRecognisedException {
        int uIdx = findUnitIndex(unitId);
        if (uIdx == -1) throw new IDNotRecognisedException("Unit not found");
        return formatUnit(units[uIdx]);
    }

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

    @Override
    public int[] getIncidentIds() {
        int[] ids = new int[incidentCount];
        for (int i = 0; i < incidentCount; i++)
            ids[i] = incidents[i].getIncidentId();
        sortAscending(ids);
        return ids;
    }

    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException {
        int iIdx = findIncidentIndex(incidentId);
        if (iIdx == -1) throw new IDNotRecognisedException("Incident not found");
        return formatIncident(incidents[iIdx]);
    }

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

    @Override
    public void tick() {
        currentTick++;
        int[] sortedUnitIndices     = getSortedUnitIndices();
        int[] sortedIncidentIndices = getSortedIncidentIndices();

        // move EN_ROUTE units by ascending based on unitId
        for (int i = 0; i < unitCount; i++) {
            Unit unit = units[sortedUnitIndices[i]];
            if (unit.getStatus() != UnitStatus.EN_ROUTE) continue;
            Incident target = incidents[findIncidentIndex(unit.getAssignedIncidentId())];
            unit.moveTowards(target.getX(), target.getY(), cityMap);
        }

        //mark arrivals by ascending based on unitId
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

        // This increments on-scene work by ascending through unitId
        for (int i = 0; i < unitCount; i++) {
            Unit unit = units[sortedUnitIndices[i]];
            if (unit.getStatus() == UnitStatus.AT_SCENE)
                unit.setTicksWorkedAtScene(unit.getTicksWorkedAtScene() + 1);
        }

        // resolve completed incidents by ascending incidentId
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

             private String formatIncident(Incident incident) {
        String unitStr = incident.getAssignedUnitId() == -1 ? "-"
                       : String.valueOf(incident.getAssignedUnitId());
        String typeStr = incident.getType().toString().replace("_", "");
        String statusStr = incident.getStatus().toString().replace("_", "");
        return String.format("I#%d TYPE=%s SEV=%d LOC=(%d,%d) STATUS=%s UNIT=%s",
            incident.getIncidentId(), typeStr, incident.getSeverity(),
            incident.getX(), incident.getY(), statusStr, unitStr);
    }

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


    private int findStationIndex(int stationId) {
        for (int i = 0; i < stationCount; i++)
            if (stations[i].getStationId() == stationId) return i;
        return -1;
    }

    private int findUnitIndex(int unitId) {
        for (int i = 0; i < unitCount; i++)
            if (units[i].getUnitId() == unitId) return i;
        return -1;
    }

    private int findIncidentIndex(int incidentId) {
        for (int i = 0; i < incidentCount; i++)
            if (incidents[i].getIncidentId() == incidentId) return i;
        return -1;
    }

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

    private void sortAscending(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i]; int j = i - 1;
            while (j >= 0 && arr[j] > key) { arr[j + 1] = arr[j]; j--; }
            arr[j + 1] = key;
        }
    }

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

    private Unit createUnit(int unitId, UnitType type, int homeStationId, int x, int y) {
        switch (type) {
            case AMBULANCE:   return new Ambulance(unitId, homeStationId, x, y);
            case FIRE_ENGINE: return new FireEngine(unitId, homeStationId, x, y);
            case POLICE_CAR:  return new PoliceCar(unitId, homeStationId, x, y);
            default: throw new IllegalArgumentException("Unknown unit type");
        }
    }
}

