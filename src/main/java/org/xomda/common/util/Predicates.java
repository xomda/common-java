package org.xomda.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Utils for creating and working with {@link Predicate predicates}.
 */
public class Predicates {

	private static final Predicate<?> ALWAYS_TRUE = always(true);
	private static final Predicate<?> ALWAYS_FALSE = always(false);

	public static <T> Predicate<T> always(final boolean result) {
		return (final T value) -> result;
	}

	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> alwaysTrue() {
		return (Predicate<T>) ALWAYS_TRUE;
	}

	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> alwaysFalse() {
		return (Predicate<T>) ALWAYS_FALSE;
	}

	/**
	 * @see Predicate#not(Predicate)
	 */
	public static <T> Predicate<T> not(Predicate<T> predicate) {
		return Predicate.not(predicate);
	}

	/**
	 * @see Predicate#not(Predicate)
	 */
	public static <T> Predicate<T> not(Function<T, Boolean> predicate) {
		return Predicate.not(of(predicate));
	}

	/**
	 * Helper to create a {@link Predicate} out of a {@link Boolean}-returning method.
	 */
	public static <T> Predicate<T> of(Function<T, Boolean> predicate) {
		return predicate::apply;
	}

	/**
	 * Combines multiple {@link Predicate predicates} using logical OR.
	 */
	public static <T> Predicate<T> or(Predicate<T>... predicates) {
		return predicates == null
				? alwaysTrue()
				: Stream.of(predicates).reduce(alwaysFalse(), Predicate::or);
	}

	/**
	 * Combines multiple {@link Predicate predicates} using logical AND.
	 */
	public static <T> Predicate<T> and(Predicate<T>... predicates) {
		return predicates == null
				? alwaysTrue()
				: Stream.of(predicates).reduce(alwaysTrue(), Predicate::and);
	}

	/**
	 * Helper to create a null-safe {@link Predicate} out of a {@link Boolean}-returning method,
	 * because <code>Boolean</code> — in contrast to a <code>boolean</code> — can be null too,
	 * which would cause unwanted {@link NullPointerException NPE}'s.
	 */
	public static <T> Predicate<T> safe(Function<T, Boolean> predicate) {
		return (T value) -> Boolean.TRUE.equals(predicate.apply(value));
	}

	/**
	 * @see Predicate#isEqual(Object)
	 */
	public static <T> Predicate<T> isEqual(Object value) {
		return Predicate.isEqual(value);
	}

	/**
	 * Evaluate a sub-property of the evaluated input object. For example, when processing a stream of persons,
	 * and you want to filter out the persons with a name called "John". Then you could say:
	 * <code><pre>
	 *     Stream.of(persons)
	 *     	.filter(against(Person::getName, "John"))
	 *     	.toList();
	 * </pre></code>
	 */
	public static <T, V> Predicate<T> against(Function<T, V> getter, V value) {
		return against(getter, isEqual(value));
	}

	/**
	 * Evaluate a sub-property of the evaluated input object. For example, when processing a stream of persons,
	 * and you want to filter out the persons with a name called "John". Then you could say:
	 * <code><pre>
	 *     Stream.of(persons)
	 *     	.filter(against(Person::getName, isEqual("John")))
	 *     	.toList();
	 * </pre></code>
	 */
	public static <T, V> Predicate<T> against(Function<T, V> getter, Predicate<V> predicate) {
		return (T value) -> predicate.test(getter.apply(value));
	}

	/**
	 * The default strategy for caching {@link Predicate}s.
	 */
	public static class DefaultPredicateCache<T> extends ConcurrentHashMap<T, Boolean> {
	}

	/**
	 * Cache the outcome of a {@link Predicate} so it doesn't need to be evaluated all the time.
	 * The provided cacheSupplier is used to create the cache, such that the user has freedom into
	 * which implementation is used. By default, a {@link DefaultPredicateCache} will be used for cache.
	 */
	public static <T> Predicate<T> cached(Predicate<T> predicate, Supplier<Map<T, Boolean>> cacheSupplier) {
		Map<T, Boolean> cache = cacheSupplier.get();
		AtomicReference<Boolean> nullValue = new AtomicReference<>(null);
		return (T value) -> null == value
				? nullValue.updateAndGet(b -> null != b ? b : predicate.test(value))
				: cache.computeIfAbsent(value, k -> predicate.test(value));
	}

	/**
	 * Cache the outcome of a {@link Predicate} so it doesn't need to be evaluated all the time
	 */
	public static <T> Predicate<T> cached(Predicate<T> predicate) {
		return cached(predicate, DefaultPredicateCache::new);
	}

	private Predicates() {
	}
}
