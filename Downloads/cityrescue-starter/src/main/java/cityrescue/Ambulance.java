package cityrescue;

import cityrescue.enums.UnitType;
import cityrescue.enums.IncidentType;

public class Ambulance extends Unit {
    public Ambulance(int unitId, int homeStationId, int startX, int startY) {
        super(unitId, UnitType.AMBULANCE, homeStationId, startX, startY);
    }

    @Override
    public boolean canHandleIncidentType(IncidentType incidentType) {
        return incidentType == IncidentType.MEDICAL;
    }

    @Override
    public int getTicksToResolve() { return 2; }
}
