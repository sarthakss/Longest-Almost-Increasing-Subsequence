package print;

import java.util.concurrent.atomic.AtomicLong;

public class Counter{
    static private AtomicLong cnt;

    static public void reset() {
        cnt = new AtomicLong();
    }

    static public long get() {
        return cnt.get();
    }

    static public long increment() {
        if (cnt == null)
            reset();

        return cnt.incrementAndGet();
    }
}