package org.xomda.common.function;

import static org.xomda.common.exception.SneakyThrow.throwSneaky;

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

	/**
	 * A Cache which will block pushing to it, until there is room again.
	 */
	static class LimitedBlockingCache<T> extends DefaultCache<T> {

		public final static int DEFAULT_SIZE = 1024;

		private final int limit;
		private final Object lock = new Object();

		LimitedBlockingCache() {
			this(DEFAULT_SIZE);
		}

		LimitedBlockingCache(int limit) {
			if (limit < 0) {
				throw new IllegalArgumentException("A maximum size below 1 does not make send");
			}
			this.limit = limit;
		}

		@Override
		public synchronized void push(final T t) {
			while (super.size() >= limit)
				;
			super.push(t);
		}
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
			thread.setUncaughtExceptionHandler((t, e) -> throwSneaky(e));
		}

		private void waitFor() {
			while (!done && queue.isEmpty())
				;
		}

		private void startIfNeeded() {
			if (thread.started) {
				return;
			}
			synchronized (this) {
				if (!thread.started) {
					thread.start();
				}
			}
		}

		@Override
		public boolean hasNext() {
			startIfNeeded();
			if (done && queue.isEmpty()) {
				return false;
			}
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

	public static <T> Deque<T> blockingCache() {
		return new LimitedBlockingCache<>();
	}

	public static <T> Deque<T> blockingCache(int limit) {
		return new LimitedBlockingCache<>(limit);
	}

}
