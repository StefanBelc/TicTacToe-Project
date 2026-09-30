package cv.portofolio.service.infrastructure;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DurationStopWatchTest {

    @Test
    void measuresElapsedTimeBetweenStartAndStop() throws InterruptedException {
        DurationStopWatch stopWatch = new DurationStopWatch();

        stopWatch.start();
        Thread.sleep(30);
        stopWatch.stop();

        assertThat(stopWatch.getDuration()).isGreaterThanOrEqualTo(25);
    }

    @Test
    void stopIsIdempotentSoDurationDoesNotGrowAfterStop() throws InterruptedException {
        DurationStopWatch stopWatch = new DurationStopWatch();
        stopWatch.start();
        stopWatch.stop();
        long first = stopWatch.getDuration();

        Thread.sleep(20);
        stopWatch.stop(); // second stop must not overwrite the first end time

        assertThat(stopWatch.getDuration()).isEqualTo(first);
    }

    @Test
    void resetAllowsTheWatchToBeReused() throws InterruptedException {
        DurationStopWatch stopWatch = new DurationStopWatch();
        stopWatch.start();
        Thread.sleep(40);
        stopWatch.stop();

        stopWatch.reset();
        stopWatch.start();
        stopWatch.stop();

        assertThat(stopWatch.getDuration()).isLessThan(40);
    }
}
