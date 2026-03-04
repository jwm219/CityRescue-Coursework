package cityrescue;

import cityrescue.enums.UnitType;
import cityrescue.enums.IncidentType;

public class FireEngine extends Unit {
    public FireEngine(int unitId, int homeStationId, int startX, int startY) {
        super(unitId, UnitType.FIRE_ENGINE, homeStationId, startX, startY);
    }

    @Override
    public boolean canHandleIncidentType(IncidentType incidentType) {
        return incidentType == IncidentType.FIRE;
    }

    @Override
    public int getTicksToResolve() { return 4; }
}
