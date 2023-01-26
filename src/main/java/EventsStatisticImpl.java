import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class EventsStatisticImpl implements EventsStatistic {

    private final Clock clock;
    private final Map<String, ArrayList<Instant>> events;

    public EventsStatisticImpl(Clock clock) {
        this.clock = clock;
        this.events = new HashMap<>();
    }

    @Override
    public void incEvent(String name) {
        if (!events.containsKey(name)) {
            events.put(name, new ArrayList<>());
        }
        events.get(name).add(clock.now());
    }

    @Override
    public double getEventStatisticByName(String name) {
        if (!events.containsKey(name)) {
            return 0.0;
        }
        Instant start = clock.now().minus(Duration.ofHours(1));
        int pos = Arrays.binarySearch(events.get(name).toArray(), start);
        if (pos == -events.get(name).size() - 1) {
            events.get(name).clear();
            return 0.0;
        } else {
            return (pos >= 0 ? (events.get(name).size() - pos) : (events.get(name).size() - (-pos - 1))) / 60.0;
        }
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        Map<String, Double> res = new HashMap<>();
        events.forEach((name, q) -> res.put(name, getEventStatisticByName(name)));
        return res;
    }

    @Override
    public void printStatistic() {
        getAllEventStatistic().forEach((name, val) -> System.out.println(name + ": " + val));
    }
}
