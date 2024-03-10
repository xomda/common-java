package org.xomda.common.function;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.xomda.common.exception.SneakyThrow;

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

	private Functions() {
	}

}
