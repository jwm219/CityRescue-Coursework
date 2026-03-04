package cityrescue;

import cityrescue.enums.UnitType;
import cityrescue.enums.IncidentType;

//police unit for crime emergencies
public class PoliceCar extends Unit {

    //sets police unit with station and start position
    public PoliceCar(int unitId, int homeStationId, int startX, int startY) {
        super(unitId, UnitType.POLICE_CAR, homeStationId, startX, startY);
    }

    //checks that unit can deal with incident
    @Override
    public boolean canHandleIncidentType(IncidentType incidentType) {
        return incidentType == IncidentType.CRIME;
    }

    //returns ticks taken to resolve
    @Override
    public int getTicksToResolve() { return 3; }
}
