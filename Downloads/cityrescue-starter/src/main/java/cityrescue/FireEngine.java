package cityrescue;

import cityrescue.enums.UnitType;
import cityrescue.enums.IncidentType;

//fire engine unit for fire incidents
public class FireEngine extends Unit {

    //set up fire engine with station and start position
    public FireEngine(int unitId, int homeStationId, int startX, int startY) {
        super(unitId, UnitType.FIRE_ENGINE, homeStationId, startX, startY);
    }

    //checks that unit can deal with incident
    @Override
    public boolean canHandleIncidentType(IncidentType incidentType) {
        return incidentType == IncidentType.FIRE;
    }

    //returns ticks taken to resolve
    @Override
    public int getTicksToResolve() { return 4; }
}
