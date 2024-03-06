package org.xomda.common.function;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * An abstract implementation of a {@link Stream} that delegates all functionality to a delegate stream.
 *
 * @param <T> type of the delegate {@link Stream}
 */
public abstract class DelegateStream<T> implements Stream<T> {

	/**
	 * @return The underlying stream toward all traffic is redirected
	 */
	abstract Stream<T> delegate();

	@Override
	public Stream<T> filter(Predicate<? super T> predicate) {
		return delegate().filter(predicate);
	}

	@Override
	public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
		return delegate().map(mapper);
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super T> mapper) {
		return delegate().mapToInt(mapper);
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super T> mapper) {
		return delegate().mapToLong(mapper);
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		return delegate().mapToDouble(mapper);
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		return delegate().flatMap(mapper);
	}

	@Override
	public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		return delegate().flatMapToInt(mapper);
	}

	@Override
	public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		return delegate().flatMapToLong(mapper);
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		return delegate().flatMapToDouble(mapper);
	}

	@Override
	public <R> Stream<R> mapMulti(BiConsumer<? super T, ? super Consumer<R>> mapper) {
		return delegate().mapMulti(mapper);
	}

	@Override
	public IntStream mapMultiToInt(BiConsumer<? super T, ? super IntConsumer> mapper) {
		return delegate().mapMultiToInt(mapper);
	}

	@Override
	public LongStream mapMultiToLong(BiConsumer<? super T, ? super LongConsumer> mapper) {
		return delegate().mapMultiToLong(mapper);
	}

	@Override
	public DoubleStream mapMultiToDouble(BiConsumer<? super T, ? super DoubleConsumer> mapper) {
		return delegate().mapMultiToDouble(mapper);
	}

	@Override
	public Stream<T> distinct() {
		return delegate().distinct();
	}

	@Override
	public Stream<T> sorted() {
		return delegate().sorted();
	}

	@Override
	public Stream<T> sorted(Comparator<? super T> comparator) {
		return delegate().sorted(comparator);
	}

	@Override
	public Stream<T> peek(Consumer<? super T> action) {
		return delegate().peek(action);
	}

	@Override
	public Stream<T> limit(long maxSize) {
		return delegate().limit(maxSize);
	}

	@Override
	public Stream<T> skip(long n) {
		return delegate().skip(n);
	}

	@Override
	public Stream<T> takeWhile(Predicate<? super T> predicate) {
		return delegate().takeWhile(predicate);
	}

	@Override
	public Stream<T> dropWhile(Predicate<? super T> predicate) {
		return delegate().dropWhile(predicate);
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		delegate().forEach(action);
	}

	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		delegate().forEachOrdered(action);
	}

	@Override
	public Object[] toArray() {
		return delegate().toArray();
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		return delegate().toArray(generator);
	}

	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		return delegate().reduce(identity, accumulator);
	}

	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		return delegate().reduce(accumulator);
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		return delegate().reduce(identity, accumulator, combiner);
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		return delegate().collect(supplier, accumulator, combiner);
	}

	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		return delegate().collect(collector);
	}

	@Override
	public List<T> toList() {
		return delegate().toList();
	}

	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		return delegate().min(comparator);
	}

	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		return delegate().max(comparator);
	}

	@Override
	public long count() {
		return delegate().count();
	}

	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		return delegate().anyMatch(predicate);
	}

	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		return delegate().allMatch(predicate);
	}

	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		return delegate().noneMatch(predicate);
	}

	@Override
	public Optional<T> findFirst() {
		return delegate().findFirst();
	}

	@Override
	public Optional<T> findAny() {
		return delegate().findAny();
	}

	public static <T1> Builder<T1> builder() {
		return Stream.builder();
	}

	public static <T1> Stream<T1> empty() {
		return Stream.empty();
	}

	public static <T1> Stream<T1> of(T1 t1) {
		return Stream.of(t1);
	}

	public static <T1> Stream<T1> ofNullable(T1 t1) {
		return Stream.ofNullable(t1);
	}

	@SafeVarargs
	public static <T1> Stream<T1> of(T1... values) {
		return Stream.of(values);
	}

	public static <T1> Stream<T1> iterate(T1 seed, UnaryOperator<T1> f) {
		return Stream.iterate(seed, f);
	}

	public static <T1> Stream<T1> iterate(T1 seed, Predicate<? super T1> hasNext, UnaryOperator<T1> next) {
		return Stream.iterate(seed, hasNext, next);
	}

	public static <T1> Stream<T1> generate(Supplier<? extends T1> s) {
		return Stream.generate(s);
	}

	public static <T1> Stream<T1> concat(Stream<? extends T1> a, Stream<? extends T1> b) {
		return Stream.concat(a, b);
	}

	@Override
	public Iterator<T> iterator() {
		return delegate().iterator();
	}

	@Override
	public Spliterator<T> spliterator() {
		return delegate().spliterator();
	}

	@Override
	public boolean isParallel() {
		return delegate().isParallel();
	}

	@Override
	public Stream<T> sequential() {
		return delegate().sequential();
	}

	@Override
	public Stream<T> parallel() {
		return delegate().parallel();
	}

	@Override
	public Stream<T> unordered() {
		return delegate().unordered();
	}

	@Override
	public Stream<T> onClose(Runnable closeHandler) {
		return delegate().onClose(closeHandler);
	}

	@Override
	public void close() {
		delegate().close();
	}
}
