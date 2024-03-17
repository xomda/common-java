package org.xomda.common.util.stream;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utils that provide functionality for when working with {@link java.util.stream.Stream streams}.
 */
public final class Streams {

	/**
	 * Returns a {@link BufferedStream buffered version} of the given {@link Stream stream}.
	 *
	 * @see BufferedStream#BufferedStream(Stream) BufferedStream(Stream)
	 * @see BufferedStream
	 */
	public static <T> Stream<T> buffered(Stream<T> stream) {
		return new BufferedStream<>(stream);
	}

	/**
	 * Returns a {@link BufferedStream buffered version} of the given {@link Stream stream},
	 * using the provided maximum cache size.
	 *
	 * @see BufferedStream#BufferedStream(Stream, int) BufferedStream(Stream, int)
	 * @see BufferedStream
	 */
	public static <T> Stream<T> buffered(Stream<T> stream, int maxStackSize) {
		return new BufferedStream<>(stream, maxStackSize);
	}

	/**
	 * Concatenate multiple {@link Stream streams} into one.
	 * The internal implementation depends on how many streams are being concatenated.
	 */
	@SafeVarargs
	public static <T> Stream<T> concat(Stream<T>... streams) {
		if (null == streams || streams.length == 0) {
			return Stream.empty();
		}
		if (streams.length == 1) {
			return streams[0];
		}
		return streams.length == 2
				? Stream.concat(streams[0], streams[1])
				: Stream.of(streams)
						.reduce(Stream::concat)
						.orElseGet(Stream::empty);
	}

	/**
	 * Short way to create a {@link Stream stream} out of an {@link Iterator iterator},
	 * while still specifying whether it has to be a parallel stream or not.
	 */
	public static <T> Stream<T> stream(Iterator<T> it, boolean parallel) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED), parallel);
	}

	/**
	 * Short way to create a sequential {@link Stream stream} out of an {@link Iterator iterator}.
	 */
	public static <T> Stream<T> stream(Iterator<T> it) {
		return stream(it, false);
	}

	/**
	 * Short way to create a sequential {@link Stream stream} out of an {@link Iterable iterable},
	 * while still specifying whether it has to be a parallel stream or not.
	 */
	public static <T> Stream<T> stream(Iterable<T> it, boolean parallel) {
		return StreamSupport.stream(it.spliterator(), parallel);
	}

	/**
	 * Short way to create a sequential {@link Stream stream} out of an {@link Iterable iterable}.
	 */
	public static <T> Stream<T> stream(Iterable<T> it) {
		return stream(it, false);
	}

	/**
	 * Create a cascading stream, starting with the first element,
	 * proceeding with the outcome of the "next" {@link UnaryOperator unary operator}.
	 * It will run as long as the returned value isn't null, or the parent itself.
	 */
	public static <T> Stream<T> cascading(T start, UnaryOperator<T> next) {
		return stream(new CascadingIterator<>(start, next));
	}

	/**
	 * Create a cascading stream, starting with the first element,
	 * proceeding with the outcome of the "next" {@link UnaryOperator unary operator}.
	 * It will run as long as the returned optional isn't empty.
	 */
	public static <T> Stream<T> cascading(T start, Function<T, Optional<T>> next) {
		return stream(new CascadingIterator<>(start, v -> next.apply(v).orElse(null)));
	}

	// Helper class for creating s cascading stream
	private static class CascadingIterator<T> implements Iterator<T> {
		private final AtomicReference<T> current = new AtomicReference<>();
		private final UnaryOperator<T> next;

		CascadingIterator(T start, UnaryOperator<T> next) {
			this.next = next;
			current.set(start);
		}

		@Override
		public synchronized boolean hasNext() {
			return current.get() != null;
		}

		@Override
		public synchronized T next() {
			T result = current.getAndUpdate(next);
			if (null == result) {
				throw new NoSuchElementException();
			}
			if (result == current.get()) {
				current.set(null);
			}
			return result;
		}
	}

	private Streams() {
	}
}
