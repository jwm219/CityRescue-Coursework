package cityrescue;

import cityrescue.enums.UnitType;
import cityrescue.enums.IncidentType;

public class PoliceCar extends Unit {
    public PoliceCar(int unitId, int homeStationId, int startX, int startY) {
        super(unitId, UnitType.POLICE_CAR, homeStationId, startX, startY);
    }

    @Override
    public boolean canHandleIncidentType(IncidentType incidentType) {
        return incidentType == IncidentType.CRIME;
    }

    @Override
    public int getTicksToResolve() { return 3; }
}
