package org.xomda.common.util.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.xomda.common.util.stream.Streams.cascading;
import static org.xomda.common.util.stream.Streams.concat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class StreamsTest {

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testConcat() {
		List<String> ab = List.of("a", "b");
		List<String> cd = List.of("c", "d");
		List<String> ef = List.of("e", "f");
		List<String> gh = List.of("g", "h");
		List<String> ij = List.of("i", "j");

		assertEquals(Stream.empty().toList(), concat().toList());
		assertEquals(Stream.empty().toList(), concat((Stream[]) null).toList());
		assertEquals("", concatAll());
		assertEquals("ab", concatAll(ab));
		assertEquals("abcd", concatAll(ab, cd));
		assertEquals("abcdef", concatAll(ab, cd, ef));
		assertEquals("abcdefghij", concatAll(ab, cd, ef, gh, ij));
	}

	@SuppressWarnings("unchecked")
	static String concatAll(List<String>... lists) {
		Stream<String>[] streams = Stream.of(lists)
				.map(List::stream)
				.toArray(Stream[]::new);
		return concat(streams)
				.collect(Collectors.joining());
	}

	private static class TestParent {
		private final TestParent parent;

		TestParent(TestParent parent) {
			this.parent = parent;
		}

		Optional<TestParent> findParent() {
			return Optional.ofNullable(getParent());
		}

		TestParent getParent() {
			return parent;
		}

	}

	@Test
	public void testCascading() {
		TestParent a = new TestParent(null);
		TestParent b = new TestParent(a);
		TestParent c = new TestParent(b);
		TestParent d = new TestParent(c);
		TestParent e = new TestParent(d);

		testCascading(a, b, c, d, e);
	}

	@Test
	public void testCyclicCascading() {
		TestParent a = new TestParent(null) {
			TestParent getParent() {
				return this;
			}
		};
		TestParent b = new TestParent(a);
		TestParent c = new TestParent(b);
		TestParent d = new TestParent(c);
		TestParent e = new TestParent(d);

		testCascading(a, b, c, d, e);
	}

	private void testCascading(TestParent... parents) {
		List<TestParent> parentList = List.of(parents);
		IntStream.range(0, parentList.size() + 1).forEach(i -> {
			List<TestParent> expected = new ArrayList<>(parentList.subList(0, i));
			Collections.reverse(expected);
			TestParent testParent = i == 0 ? null : expected.get(0);
			// regular
			assertEquals(expected, cascading(testParent, TestParent::getParent).toList());
			// using Optional<T>
			assertEquals(expected, cascading(testParent, TestParent::findParent).toList());
		});
	}

}