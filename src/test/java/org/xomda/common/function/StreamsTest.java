package org.xomda.common.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.xomda.common.function.Streams.concat;

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

		assertEquals(Stream.empty().toList(), concat().toList());
		assertEquals(Stream.empty().toList(), concat((Stream[]) null).toList());
		assertEquals("", Streams.<String> concat().collect(Collectors.joining()));
		assertEquals("ab", concat(ab.stream()).collect(Collectors.joining()));
		assertEquals("abcd", concat(ab.stream(), cd.stream()).collect(Collectors.joining()));
		assertEquals("abcdef", concat(ab.stream(), cd.stream(), ef.stream()).collect(Collectors.joining()));
	}

}
