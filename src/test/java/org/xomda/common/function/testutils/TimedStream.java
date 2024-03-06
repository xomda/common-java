package org.xomda.common.function.testutils;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TimedStream implements Iterator<Integer> {

    private long waitTime = 100L;
    private int count = 100;

    private Object lock = new Object();

    @Override
    public boolean hasNext() {
        return count > 0;
    }

    @Override
    public Integer next() {
        synchronized (lock) {
            try {
                lock.wait(waitTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return --count;
    }

    public Stream<Integer> create() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
    }

    public static Stream<Integer> create(int waitTime, int count) {
        TimedStream ts = new TimedStream();
        ts.waitTime = waitTime;
        ts.count = count;
        return ts.create();
    }

}
