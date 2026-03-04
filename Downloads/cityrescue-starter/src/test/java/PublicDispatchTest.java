import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import cityrescue.*;
import cityrescue.enums.*;
import cityrescue.exceptions.*;

public class PublicDispatchTest {
    private CityRescue cr;

    @BeforeEach
    void setUp() throws Exception {
        cr = new CityRescueImpl();
        cr.initialise(6, 6);
    }

    @Test
    void dispatch_assignsClosestEligibleUnit_thenLowestUnitId() throws Exception {
        int s = cr.addStation("A", 0, 0);
        int u1 = cr.addUnit(s, UnitType.POLICE_CAR);
        int u2 = cr.addUnit(s, UnitType.POLICE_CAR);

        int i1 = cr.reportIncident(IncidentType.CRIME, 2, 2, 2);

        cr.dispatch();

        String inc = cr.viewIncident(i1);
        assertTrue(inc.contains("UNIT=" + u1));
        assertFalse(inc.contains("UNIT=" + u2));
    }
}
