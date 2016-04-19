package tests.model;

import model.*;
import org.junit.*;

/**
 * @author nilsw
 */
public class TimelineTest {
    Timeline timeline;
    Status   result;

    @Before
    public void setUp() throws Exception {
        timeline = new Timeline();
        TimelineEvent evt = new TimelineEvent("evt", 10000, 20000);
        timeline.addEvent(evt);
    }

    @Test
    public void getStatus() throws Exception {
        result = timeline.getStatus(0);
        Assert.assertEquals(Status.NO_EVENT, result);

        result = timeline.getStatus(9500);
        Assert.assertEquals(Status.BEFORE_EVENT, result);

        result = timeline.getStatus(15000);
        Assert.assertEquals(Status.IN_EVENT, result);

        result = timeline.getStatus(20001);
        Assert.assertEquals(Status.NO_EVENT, result);
    }
}
