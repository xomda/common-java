package org.xomda.common.util;

import java.util.function.Function;
import java.util.function.Predicate;

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

	private Predicates() {
	}
}
