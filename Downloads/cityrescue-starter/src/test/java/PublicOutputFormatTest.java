import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import cityrescue.*;
import cityrescue.enums.*;
import cityrescue.exceptions.*;

public class PublicOutputFormatTest {
    private CityRescue cr;

    @BeforeEach
    void setUp() throws Exception {
        cr = new CityRescueImpl();
        cr.initialise(5, 5);
    }

    @Test
    void getStatus_containsRequiredHeadings() throws Exception {
        String s = cr.getStatus();
        assertTrue(s.contains("TICK="));
        assertTrue(s.contains("INCIDENTS"));
        assertTrue(s.contains("UNITS"));
    }

    @Test
    void viewUnit_and_viewIncident_haveStablePrefixes() throws Exception {
        int st = cr.addStation("A", 0, 0);
        int u = cr.addUnit(st, UnitType.FIRE_ENGINE);
        int i = cr.reportIncident(IncidentType.FIRE, 3, 4, 4);

        // Output prefix must match the coursework specification examples
        // (e.g., "U#2 ..." and "I#1 ...").
        assertTrue(cr.viewUnit(u).startsWith("U#"));
        assertTrue(cr.viewIncident(i).startsWith("I#"));
    }
}
