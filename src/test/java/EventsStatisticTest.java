import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class EventsStatisticTest extends Assert {
    private static final double EPS = 1e-8;
    private static final Duration HOUR = Duration.ofHours(1);
    private static final Duration MINUTE = Duration.ofMinutes(1);

    private static final ArrayList<String> MOCK_EVENTS = new ArrayList<String>(Arrays.asList("Event 1", "Event 2", "Event 3"));

    private MockClock clock;
    private EventsStatistic eventsStatistic;

    @Test
    public void testEmpty() {
        assertEquals(0.0, eventsStatistic.getEventStatisticByName(MOCK_EVENTS.get(1)), EPS);
    }

    @Before
    public void setUp() {
        clock = new MockClock(Instant.now());
        eventsStatistic = new EventsStatisticImpl(clock);
    }

    @Test
    public void testWithoutClock() {
        String event1 = MOCK_EVENTS.get(1), event2 = MOCK_EVENTS.get(2);
        eventsStatistic.incEvent(event1);
        eventsStatistic.incEvent(event2);
        eventsStatistic.incEvent(event1);
        assertEquals(2 / 60.0, eventsStatistic.getEventStatisticByName(event1), EPS);
        assertEquals(1 / 60.0, eventsStatistic.getEventStatisticByName(event2), EPS);
    }

    @Test
    public void testClock() {
        String event1 = MOCK_EVENTS.get(1);
        eventsStatistic.incEvent(event1);
        clock.incBy(HOUR.plus(MINUTE));
        assertEquals(0.0, eventsStatistic.getEventStatisticByName(event1), EPS);
        eventsStatistic.incEvent(event1);
        clock.incBy(HOUR);
        assertEquals(1 / 60.0, eventsStatistic.getEventStatisticByName(event1), EPS);
        clock.incBy(MINUTE);
        assertEquals(0.0, eventsStatistic.getEventStatisticByName(event1), EPS);
    }

    @Test
    public void testHourDelta() {
        String event1 = MOCK_EVENTS.get(1);
        eventsStatistic.incEvent(event1);
        clock.incBy(MINUTE);
        eventsStatistic.incEvent(event1);
        assertEquals(2 / 60.0, eventsStatistic.getEventStatisticByName(event1), EPS);
        clock.incBy(HOUR);
        assertEquals(1 / 60.0, eventsStatistic.getEventStatisticByName(event1), EPS);
        clock.incBy(MINUTE);
        assertEquals(0.0, eventsStatistic.getEventStatisticByName(event1), EPS);
    }

    @Test
    public void testMassiveWithoutClock() {
        for (int k = 0, n = 10; k < 3; ++k, n *= 10) {
            for (int i = 0; i < n; ++i) {
                eventsStatistic.incEvent(MOCK_EVENTS.get(k));
            }
        }
        Map<String, Double> ans = eventsStatistic.getAllEventStatistic();
        for (int k = 0, n = 10; k < 3; ++k, n *= 10) {
            for (int i = 0; i < n; ++i) {
                assertEquals(n / 60.0, ans.get(MOCK_EVENTS.get(k)), EPS);
            }
        }
    }

    @Test
    public void testMassive() {
        String event0 = MOCK_EVENTS.get(0), event1 = MOCK_EVENTS.get(1), event2 = MOCK_EVENTS.get(2);
        eventsStatistic.incEvent(event0);
        clock.incBy(MINUTE);
        for (int i = 0; i < 60; ++i) {
            eventsStatistic.incEvent(event1);
            clock.incBy(MINUTE);
        }
        eventsStatistic.incEvent(event2);
        clock.incBy(MINUTE);
        eventsStatistic.incEvent(event2);
        clock.incBy(MINUTE);
        Map<String, Double> ans = eventsStatistic.getAllEventStatistic();
        assertEquals(0.0, ans.get(event0), EPS);
        assertEquals(58 / 60.0, ans.get(event1), EPS);
        assertEquals(2 / 60.0, ans.get(event2), EPS);
    }
}
