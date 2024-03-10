package org.xomda.common.util.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Possibly getting another name, to avoid conflicts with {@link java.util.stream.Collectors java.util.stream.Collectors}.
 * Otherwise, the end user would be forced to <code>static import</code> these methods.
 * <br/>
 * Collectors is a class
 */
public final class Collectors {

	static class StreamCollector<T> implements Collector<T, List<T>, Stream<T>> {

		@Override
		public Supplier<List<T>> supplier() {
			return ArrayList::new;
		}

		@Override
		public BiConsumer<List<T>, T> accumulator() {
			return List::add;
		}

		@Override
		public BinaryOperator<List<T>> combiner() {
			return (a, b) -> {
				a.addAll(b);
				return a;
			};
		}

		@Override
		public Function<List<T>, Stream<T>> finisher() {
			return List::stream;
		}

		@Override
		public Set<Collector.Characteristics> characteristics() {
			return Set.of(Collector.Characteristics.UNORDERED);
		}
	}

	public <T> Collector<T, List<T>, Stream<T>> toStream() {
		return new StreamCollector<T>();
	}

	private Collectors() {
	}
}
