import java.time.Duration;
import java.time.Instant;

public class MockClock implements Clock {
    private Instant now;

    public MockClock(Instant now) {
        this.now = now;
    }

    public void incBy(Duration duration) {
        now = now.plus(duration);
    }

    @Override
    public Instant now() {
        return now;
    }
}