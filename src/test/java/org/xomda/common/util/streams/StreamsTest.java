package org.xomda.common.util.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.xomda.common.util.stream.Streams.concat;

import java.util.List;
import java.util.stream.Collectors;
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
		assertEquals("", test());
		assertEquals("ab", test(ab));
		assertEquals("abcd", test(ab, cd));
		assertEquals("abcdef", test(ab, cd, ef));
		assertEquals("abcdefghij", test(ab, cd, ef, gh, ij));
	}

	@SuppressWarnings("unchecked")
	static String test(List<String>... lists) {
		Stream<String>[] streams = Stream.of(lists).map(List::stream).toArray(Stream[]::new);
		return concat(streams).collect(Collectors.joining());
	}

}
