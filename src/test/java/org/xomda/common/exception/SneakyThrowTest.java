package org.xomda.common.exception;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.xomda.common.exception.SneakyThrow.sneaky;
import static org.xomda.common.exception.SneakyThrow.throwSneaky;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.xomda.common.exception.SneakyThrow.ThrowingConsumer;
import org.xomda.common.exception.SneakyThrow.ThrowingFunction;

class SneakyThrowTest {

	static class TestCheckedException extends Exception {
	}

	@Test
	void testThrowSneaky() {
		assertThrowsExactly(TestCheckedException.class, () -> throwSneaky(new TestCheckedException()));
		assertThrowsExactly(IOException.class, () -> throwSneaky(new IOException()));
	}

	@Test
	void testFunction() {
		assertThrowsExactly(TestCheckedException.class, () -> map(sneaky(SneakyThrowTest::throwTestCheckedException)));
		assertThrowsExactly(
				ParseException.class,
				() -> map(sneaky((ThrowingFunction<Object, Object, Exception>) (o -> {
					throw new ParseException("bla", 0);
				})))
		);
	}

	@Test
	void testConsumer() {
		assertThrowsExactly(TestCheckedException.class, () -> consume(sneaky((ThrowingConsumer<Object, ? extends Throwable>) o -> throwTestCheckedException(null))));
	}

	static Object throwTestCheckedException(final Object args) throws TestCheckedException {
		throw new TestCheckedException();
	}

	static void consume(final Consumer<Object> consumer) {
		consumer.accept(new Object());
	}

	static void supply(final Supplier<Object> supplier) {
		supplier.get();
	}

	static void map(final Function<Object, Object> fn) {
		fn.apply(new Object());
	}

}
