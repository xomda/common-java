package org.xomda.common.util.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.xomda.common.util.stream.BufferedStream;
import org.xomda.test.utils.TimedStream;

@Execution(ExecutionMode.SAME_THREAD)
class BufferedStreamTest {

	private long startTime;
	private TestInfo testInfo;
	private PrintStream out;
	private OutputStream os;

	@BeforeEach
	void init(TestInfo testInfo) {
		this.testInfo = testInfo;
		this.startTime = System.currentTimeMillis();
		os = new ByteArrayOutputStream();
		out = new PrintStream(os);
	}

	@AfterEach
	void afterEach() throws IOException {
		try (final OutputStream os = this.os) {
			out.println("\n--------------------------------------------------------\n");
		} finally {
			System.out.print(os.toString());
		}
	}

	@Test
	void testSlowInFastOut() {
		withCache((Deque<Integer> cache) -> {
			Stream<Integer> in = TimedStream.create(20, 20);
			Stream<Integer> buffered = new BufferedStream<>(in, () -> cache);
			List<String> result = new ArrayList<>();
			buffered.forEach(i -> {
				assertNotNull(i);
				assertEquals(0, cache.size());
				result.add(log(i, cache));
			});
			out.println(String.join("\n", result));
		});
	}

	@Test
	void testFastInSlowOut() {
		withCache((Deque<Integer> cache) -> {
			Stream<Integer> in = intStream(100);
			Stream<Integer> buffered = new BufferedStream<>(in, () -> cache);
			IntConsumer waiter = waiter();
			List<String> result = new ArrayList<>();
			buffered.forEach(i -> {
				assertNotNull(i);
				waiter.accept(10);
				result.add(log(i, cache));
			});
			assertTrue(cache.isEmpty());
			out.println(String.join("\n", result));
			assertEquals(100, result.size());
		});
	}

	@Test
	void testFastInFastOut() {
		withCache((Deque<Integer> cache) -> {
			Stream<Integer> in = intStream(50);
			Stream<Integer> buffered = new BufferedStream<>(in, () -> cache);
			List<String> result = new ArrayList<>();
			buffered.forEach(i -> {
				assertNotNull(i);
				result.add(log(i, cache));
			});
			assertTrue(cache.isEmpty());
			out.println(String.join("\n", result));
			assertEquals(50, result.size());
		});
	}

	@Test
	void testSlowInSlowerOut() {
		withCache((Deque<Integer> cache) -> {
			Stream<Integer> in = TimedStream.create(10, 100);
			Stream<Integer> buffered = new BufferedStream<>(in, () -> cache);
			IntConsumer waiter = waiter();
			List<String> result = new ArrayList<>();
			buffered.forEach(i -> {
				assertNotNull(i);
				waiter.accept(15);
				result.add(log(i, cache));
			});
			assertTrue(cache.isEmpty());
			out.println(String.join("\n", result));
			assertEquals(100, result.size());
		});
	}

	@Test
	void testParallelSlowInFastOut() {
		withCache((Deque<String> cache) -> {
			Stream<String> in = Stream.concat(
					TimedStream.create(32, 64).map(i -> "%s / %s".formatted(i, 0)),
					TimedStream.create(64, 32).map(i -> "%s / %s".formatted(i, 1))
			).parallel();
			Stream<String> buffered = new BufferedStream<>(in, () -> cache);
			List<String> result = buffered.parallel().map(i -> {
						assertNotNull(i);
						return (log(i, cache));
					})
					.collect(Collectors.toList());
			out.println(String.join("\n", result));
			assertEquals(96, result.size());
		});
	}

	@Test
	void testParallelFastInSlowOut() {
		withCache((Deque<Integer> cache) -> {
			Stream<Integer> in = intStream(50).parallel();
			Stream<Integer> buffered = new BufferedStream<>(in, () -> cache);
			IntConsumer waiter = waiter();
			List<String> result = buffered.parallel().map(i -> {
						assertNotNull(i);
						waiter.accept(25);
						return (log(i, cache));
					})
					.collect(Collectors.toList());
			assertTrue(cache.isEmpty());
			out.println(String.join("\n", result));
			assertEquals(50, result.size());
		});
	}

	@Test
	void testSlowInParallelFastOut() {
		withCache((Deque<Integer> cache) -> {
			Stream<Integer> in = TimedStream.create(10, 10).parallel();
			Stream<Integer> buffered = new BufferedStream<>(in, () -> cache);
			List<String> result = buffered.parallel().map(i -> {
						assertNotNull(i);
						return (log(i, cache));
					})
					.collect(Collectors.toList());
			out.println(String.join("\n", result));
			assertEquals(10, result.size());
		});
	}

	@Test
	void testFastInParallelSlowOut() {
		withCache((Deque<Integer> cache) -> {
			Stream<Integer> in = intStream(200).parallel();
			Stream<Integer> buffered = new BufferedStream<>(in, () -> cache);
			IntConsumer waiter = waiter();
			List<String> result = buffered.parallel().map(i -> {
						assertNotNull(i);
						waiter.accept(50);
						return (log(i, cache));
					})
					.collect(Collectors.toList());
			assertTrue(cache.isEmpty());
			out.println(String.join("\n", result));
			assertEquals(200, result.size());
		});
	}

	@Test
	void testFastInParallelFastOut() {
		withCache((Deque<Integer> cache) -> {
			Stream<Integer> in = intStream(200).parallel();
			Stream<Integer> buffered = new BufferedStream<>(in, () -> cache);
			List<String> result = buffered.parallel().map(i -> {
						assertNotNull(i);
						return (log(i, cache));
					})
					.collect(Collectors.toList());
			assertTrue(cache.isEmpty());
			out.println(String.join("\n", result));
			assertEquals(200, result.size());
		});
	}

	@Test
	void testBlockingCache() {
		Stream.of(1, 8, 16).forEach(limit -> {
			int count = limit * 16;
			Deque<Integer> cache = BufferedStream.blockingCache(limit);
			Stream<Integer> in = intStream(count).parallel();
			Stream<Integer> buffered = new BufferedStream<>(in, () -> cache);
			IntConsumer waiter = waiter();
			// Fast in, slow out.
			List<String> result = buffered.map(i -> {
						assertNotNull(i);
						assertFalse(cache.size() > limit);
						waiter.accept(5);
						assertFalse(cache.size() > limit);
						return (log(i + "\t[limit: %s]".formatted(limit), cache));
					})
					.collect(Collectors.toList());
			assertTrue(cache.isEmpty());
			out.println(String.join("\n", result));
			assertEquals(count, result.size());
		});
	}

	private static IntConsumer waiter() {
		Object lock = new Object();
		return (int amount) -> {
			synchronized (lock) {
				try {
					lock.wait(amount);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	private <T> String log(Object value, Deque<T> cache) {
		return "%s\t[%s] [t: %s] [queue: %s]".formatted(
				Objects.toString(value),
				testInfo.getTestMethod().map(Method::getName).orElse("?"),
				now(),
				cache.size()
		);
	}

	private static <T> void withCache(Consumer<Deque<T>> consumer) {
		consumer.accept(createCache());
	}

	private static <T> Deque<T> createCache() {
		return new BufferedStream.DefaultCache<>();
	}

	private static Stream<Integer> intStream(int count) {
		return IntStream
				.iterate(count - 1, i -> --i)
				.limit(count)
				.boxed();
	}

	private long now() {
		return System.currentTimeMillis() - startTime;
	}

}
