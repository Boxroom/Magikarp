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
        TimelineEvent evt = new TimelineEvent("evt", 15, 20);
        timeline.addEvent(evt);
    }

    @Test
    public void getStatus() throws Exception {
        result = timeline.getStatus(0);
        Assert.assertEquals(Status.NO_EVENT, result);

        result = timeline.getStatus(14);
        Assert.assertEquals(Status.BEFORE_EVENT, result);

        result = timeline.getStatus(16);
        Assert.assertEquals(Status.IN_EVENT, result);

        result = timeline.getStatus(20.01);
        Assert.assertEquals(Status.NO_EVENT, result);
    }
}
