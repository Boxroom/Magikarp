package tests.save;

import org.junit.Before;
import org.junit.Test;
import save.Save;

/**
 * @author nilsw
 */
public class SaveTest {
    private double[] statsBefore;
    private double[] statsAfter;

    @Before
    public void setUp() throws Exception {
        //Count Alcohol Leadership Learning Team Party
        statsBefore = new double[]{100, 11, 12, 13, 14, 15};
        statsAfter = new double[]{66, 21, 22, 23, 24, 25};
    }

    @Test
    public void save() throws Exception {
        Save.save(statsBefore, statsAfter);
    }

}