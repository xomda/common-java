package org.xomda.common.util.stream;

import java.util.stream.Stream;

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

	private Streams() {
	}
}
