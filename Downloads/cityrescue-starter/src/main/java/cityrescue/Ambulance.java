package cityrescue;

import cityrescue.enums.UnitType;
import cityrescue.enums.IncidentType;

//ambulance unit for medical incidents
public class Ambulance extends Unit {
   
    //set up ambulance with station and starting position
    public Ambulance(int unitId, int homeStationId, int startX, int startY) {
        super(unitId, UnitType.AMBULANCE, homeStationId, startX, startY);
    }

    //checks that unit can deal with incident
    @Override
    public boolean canHandleIncidentType(IncidentType incidentType) {
        return incidentType == IncidentType.MEDICAL;
    }

    //returns ticks taken to resolve
    @Override
    public int getTicksToResolve() { return 2; }
}
