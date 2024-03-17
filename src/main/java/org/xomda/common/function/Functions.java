package org.xomda.common.function;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.xomda.common.exception.SneakyThrow;
import org.xomda.common.util.stream.Streams;

/**
 * Helper methods work with {@link Function functions}, 
 * such as mapping in a {@link java.util.stream.Stream#map(Function) stream} or {@link java.util.Optional#map(Function)  optional}.
 */
public final class Functions {

	/**
	 * @return a {@link Function} — commonly used with mappers — which will return the default value, provided by the {@link Function default function},
	 * when the provided mapper throws an Exception.
	 */
	public static <T, R, E extends Throwable> Function<T, R> mapOrGet(final SneakyThrow.ThrowingFunction<T, R, E> fn, final BiFunction<T, E, R> defaultFunction) {
		return (final T o) -> {
			try {
				return fn.applyThrowing(o);
			} catch (final Throwable e) {
				@SuppressWarnings("unchecked")
				E ee = (E) e;
				return defaultFunction.apply(o, ee);
			}
		};
	}

	/**
	 * @return a {@link Function} — commonly used with mappers — which will return the default value, provided by the {@link Function default function}, when the provided mapper throws an Exception.
	 * It will eat the Exception silently, so be aware.
	 * <br><br>
	 * Use {@link #mapOrGet(SneakyThrow.ThrowingFunction, BiFunction)} to get access to the Exception which was thrown.
	 */
	public static <T, R, E extends Throwable> Function<T, R> mapOrGet(final SneakyThrow.ThrowingFunction<T, R, E> fn, final Function<T, R> defaultFunction) {
		return mapOrGet(fn, (o, e) -> defaultFunction.apply(o));
	}

	/**
	 * @return a {@link Function} — commonly used with mappers — which will return the default value,
	 * when the provided mapper throws an Exception.
	 * It will eat the Exception silently, so be aware.
	 * <br><br>
	 * Use {@link #mapOrGet(SneakyThrow.ThrowingFunction, BiFunction)} to get access to the Exception which was thrown.
	 */
	public static <T, R, E extends Throwable> Function<T, R> mapOr(final SneakyThrow.ThrowingFunction<T, R, E> fn, final R defaultValue) {
		return mapOrGet(fn, (o, e) -> defaultValue);
	}

	/**
	 * @return a {@link Function} — commonly used with mappers — which will return <code>null</code> when the provided mapper throws an Exception.
	 * It will eat the Exception silently, so be aware.
	 * <br><br>
	 * Use {@link #mapOrGet(SneakyThrow.ThrowingFunction, BiFunction)} to get access to the Exception which was thrown.
	 */
	public static <T, R, E extends Throwable> Function<T, R> mapOrNull(final SneakyThrow.ThrowingFunction<T, R, E> fn) {
		return mapOr(fn, null);
	}

	/**
	 * Create a {@link Function function} that returns a {@link Stream stream} instead of an {@link Iterable iterable}.
	 * This is handy when flatMapping for example:
	 * <code><pre>
	 *     stream.flatMap(asStream(User::getShoppingList))
	 * </pre></code>
	 */
	public static <T, R> Function<T, Stream<R>> asStream(final Function<T, Iterable<R>> fn) {
		return fn.andThen(Functions::toStream);
	}

	/**
	 * Create a {@link BiFunction bi-function} that returns a {@link Stream stream} instead of an {@link Iterable iterable}.
	 */
	public static <T, U, R> BiFunction<T, U, Stream<R>> asStream(final BiFunction<T, U, Iterable<R>> fn) {
		return fn.andThen(Functions::toStream);
	}

	/**
	 * Takes an {@link Iterable iterable}, turns it into a {@link Stream stream}.
	 */
	private static <T> Stream<T> toStream(Iterable<T> it) {
		return it instanceof Collection<T> col
				? col.stream()
				: Streams.stream(it);
	}

	private Functions() {
	}

}
