import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import cityrescue.*;
import cityrescue.enums.*;
import cityrescue.exceptions.*;

public class PublicBasicsTest {
    private CityRescue cr;

    @BeforeEach
    void setUp() throws Exception {
        cr = new CityRescueImpl();
        cr.initialise(5, 5);
    }

    @Test
    void initialise_setsGridSize_andResetsTick() {
        int[] sz = cr.getGridSize();
        assertArrayEquals(new int[]{5,5}, sz);
        assertTrue(cr.getStatus().contains("TICK=0"));
    }

    @Test
    void addStation_assignsIdStartingAt1() throws Exception {
        int id1 = cr.addStation("Central", 1, 1);
        int id2 = cr.addStation("North", 1, 2);
        assertEquals(1, id1);
        assertEquals(2, id2);
    }

    @Test
    void addObstacle_outOfBounds_throws_andStateUnchanged() throws Exception {
        String before = cr.getStatus();
        assertThrows(InvalidLocationException.class, () -> cr.addObstacle(-1, 0));
        assertEquals(before, cr.getStatus());
    }
}
