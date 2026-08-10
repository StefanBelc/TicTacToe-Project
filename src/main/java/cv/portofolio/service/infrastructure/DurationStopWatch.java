package cv.portofolio.service.infrastructure;


import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class DurationStopWatch {

    private ReentrantLock lock = new ReentrantLock();

    private AtomicLong startTime = new AtomicLong(0);
    private AtomicLong endTime = new AtomicLong(0);


    public void start() {
        startTime.compareAndSet(0, System.nanoTime());
    }

    public void stop() {
        endTime.compareAndSet(0, System.nanoTime());
    }


    public long getDuration() {
        lock.lock();
        try {
            long start = startTime.get();
            long end = endTime.get();

            Long finalTime = (end == 0) ? System.nanoTime() : endTime.get();
            return TimeUnit.NANOSECONDS.toMillis(finalTime - start);
        } finally {
            lock.unlock();
        }
    }


    public void reset() {
        lock.lock();
        try {
            startTime.set(0);
            endTime.set(0);
        } finally {
            lock.unlock();
        }
    }
}
