
package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.IncidentStatus;

//represents incident in the system
public class Incident {
    private int incidentId;
    private IncidentType type;
    private int severity;
    private int x;
    private int y;
    private IncidentStatus status;
    private int assignedUnitId;

    //creates new incident. default status = REPORTED
    public Incident(int incidentId, IncidentType type, int severity, int x, int y) {
        this.incidentId = incidentId;
        this.type = type;
        this.severity = severity;
        this.x = x;
        this.y = y;
        this.status = IncidentStatus.REPORTED;
        this.assignedUnitId = -1;
    }

    //getters and setters
    public int            getIncidentId()               { return incidentId; }
    public IncidentType   getType()                     { return type; }
    public int            getSeverity()                 { return severity; }
    public void           setSeverity(int severity)     { this.severity = severity; }
    public int            getX()                        { return x; }
    public int            getY()                        { return y; }
    public IncidentStatus getStatus()                   { return status; }
    public void           setStatus(IncidentStatus s)   { this.status = s; }
    public int            getAssignedUnitId()           { return assignedUnitId; }
    public void           setAssignedUnitId(int id)     { this.assignedUnitId = id; }
}
