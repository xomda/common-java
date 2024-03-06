package org.xomda.common.function;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BufferedStream<T> extends DelegateStream<T> {

    static class DefaultCache<T> extends ConcurrentLinkedDeque<T> {
    }

    private static class InnerThread extends Thread {

        private volatile boolean started;

        InnerThread(Runnable runnable) {
            super(runnable);
        }

        @Override
        public synchronized void start() {
            started = true;
            super.start();
        }

    }

    private static class BufferedStreamException extends RuntimeException {
        BufferedStreamException(Throwable t) {
            super(t);
        }
    }

    /**
     * Iterator using a FIFO queue for polling elements
     */
    public static class Iterator<T> implements java.util.Iterator<T> {

        private final InnerThread thread;

        private final Deque<T> queue;

        // the input stream is finished
        private volatile boolean done;

        public Iterator(Stream<T> stream) {
            this(stream, DefaultCache::new);
        }

        public Iterator(Stream<T> stream, Supplier<Deque<T>> dequeSupplier) {
            this.queue = dequeSupplier.get();
            thread = new InnerThread(() -> {
                try (stream) {
                    stream.forEach(queue::push);
                    done = true;
                }
            });
            thread.setUncaughtExceptionHandler((t, e) -> {
                throw new BufferedStreamException(e);
            });
        }

        private void waitFor() {
            while (!done && queue.isEmpty()) ;
        }

        private void startIfNeeded() {
            if (thread.started) return;
            synchronized (this) {
                if (!thread.started) thread.start();
            }
        }

        @Override
        public boolean hasNext() {
            startIfNeeded();
            if (done && queue.isEmpty()) return false;
            waitFor();
            return !queue.isEmpty() || !done;
        }

        @Override
        public T next() {
            waitFor();
            if (queue.isEmpty()) {
                throw new NoSuchElementException();
            }
            return queue.pollLast();
        }

        public Stream<T> stream() {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
        }

    }

    private final Stream<T> delegate;

    public BufferedStream(Stream<T> stream, Supplier<Deque<T>> queueSupplier) {
        this.delegate = new Iterator<>(stream, queueSupplier).stream();
    }

    public BufferedStream(Stream<T> stream) {
        this.delegate = new Iterator<>(stream).stream();
    }

    @Override
    Stream<T> delegate() {
        return delegate;
    }
}
