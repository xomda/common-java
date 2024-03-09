package org.xomda.common.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.xomda.common.function.Predicates.against;
import static org.xomda.common.function.Predicates.cached;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class PredicatesTest {

	@Test
	public void testAgainst() {
		Predicate<List<?>> p = against(List::size, 4);

		assertTrue(p.test(List.of(1, 2, 3, 4)));

		assertFalse(p.test(List.of(1, 2, 3)));
		assertFalse(p.test(List.of(1, 2, 3, 4, 5)));
	}

	@Test
	public void testCached() {
		AtomicInteger counter = new AtomicInteger();
		Predicate<Object> p = cached(o -> {
			counter.incrementAndGet();
			return true;
		});

		IntStream.range(0, 100).map(i -> 0).forEach(p::test);
		// the predicate itself should have been called only once,
		// so the counter should be 1
		assertEquals(1, counter.get());

		// test using null
		p.test(null);
		assertEquals(2, counter.get());

		// another 100 executions with unique values
		IntStream.range(0, 100).forEach(p::test);
		assertEquals(101, counter.get());
	}

	@Test
	public void testCachedNull() {
		AtomicInteger counter = new AtomicInteger();
		// 1) test "true"
		Predicate<Object> p = cached(o -> {
			counter.incrementAndGet();
			return false;
		});
		// 1.1) test whether a provided null-value is cached correctly
		assertFalse(p.test(null));
		IntStream.range(0, 100).mapToObj(i -> null).forEach(p::test);
		assertEquals(1, counter.get());

		// 2) test "false"
		Predicate<Object> p2 = cached(o -> {
			counter.incrementAndGet();
			return true;
		});
		// 2.1) test whether a provided null-value is cached correctly
		assertTrue(p2.test(null));
		IntStream.range(0, 100).mapToObj(i -> null).forEach(p::test);
		assertEquals(2, counter.get());
	}

}
