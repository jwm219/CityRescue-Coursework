import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import cityrescue.*;
import cityrescue.enums.*;
import cityrescue.exceptions.*;

public class PublicTickLifecycleTest {
    private CityRescue cr;

    @BeforeEach
    void setUp() throws Exception {
        cr = new CityRescueImpl();
        cr.initialise(5, 5);
    }

    @Test
    void tick_movesUnitTowardIncident_andEventuallyResolves() throws Exception {
        int s = cr.addStation("A", 0, 0);
        int u = cr.addUnit(s, UnitType.AMBULANCE);

        int i = cr.reportIncident(IncidentType.MEDICAL, 1, 0, 1);
        cr.dispatch();

        cr.tick(); // should arrive at (0,1) in one tick
        assertTrue(cr.viewUnit(u).contains("LOC=(0,1)"));

        cr.tick();
        cr.tick();
        assertTrue(cr.viewIncident(i).contains("STATUS=RESOLVED"));
        assertTrue(cr.viewUnit(u).contains("STATUS=IDLE"));
    }
}
