package org.xomda.common.util;

import org.xomda.common.exception.SneakyThrow;
import org.xomda.common.function.BufferedStream;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Utils that provide functionality for when working with {@link java.util.stream.Stream streams}.
 */
public class StreamUtils {

    public static <T, R, E extends Throwable> Function<T, R> mapOrGet(final SneakyThrow.ThrowingFunction<T, R, E> fn, final Function<E, R> defaultSupplier) {
        return (final T o) -> {
            try {
                return fn.applyThrowing(o);
            } catch (final Throwable e) {
                @SuppressWarnings("unchecked")
                E ee = (E) e;
                return defaultSupplier.apply(ee);
            }
        };
    }

    public static <T, R, E extends Throwable> Function<T, R> mapOr(final SneakyThrow.ThrowingFunction<T, R, E> fn, final R defaultValue) {
        return mapOrGet(fn, e -> null);
    }

    public static <T, R, E extends Throwable> Function<T, R> mapOrNull(final SneakyThrow.ThrowingFunction<T, R, E> fn) {
        return mapOr(fn, null);
    }

    /**
     * Returns a {@link BufferedStream buffered version} of the given {@link Stream stream}.
     */
    public static <T> Stream<T> buffered(Stream<T> stream) {
        return new BufferedStream<>(stream);
    }

}
