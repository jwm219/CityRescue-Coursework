package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

public interface CityRescue {
    void initialise(int width, int height) throws InvalidGridException;
    int[] getGridSize();

    void addObstacle(int x, int y) throws InvalidLocationException;
    void removeObstacle(int x, int y) throws InvalidLocationException;

    int addStation(String name, int x, int y) throws InvalidNameException, InvalidLocationException;
    void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException;
    void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException;
    int[] getStationIds();

    int addUnit(int stationId, UnitType type) throws IDNotRecognisedException, InvalidUnitException, IllegalStateException;
    void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException;
    void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException;
    void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException;
    int[] getUnitIds();
    String viewUnit(int unitId) throws IDNotRecognisedException;

    int reportIncident(IncidentType type, int severity, int x, int y) throws InvalidSeverityException, InvalidLocationException;
    void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException;
    void escalateIncident(int incidentId, int newSeverity) throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException;
    int[] getIncidentIds();
    String viewIncident(int incidentId) throws IDNotRecognisedException;

    void dispatch();
    void tick();
    String getStatus();
}
