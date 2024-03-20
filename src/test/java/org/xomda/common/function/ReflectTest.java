package org.xomda.common.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.xomda.common.function.Predicates.against;
import static org.xomda.common.function.Predicates.testAgainst;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.xomda.common.reflect.Reflect;

public class ReflectTest {

	@Test
	public void testFindMethods() {
		List<Method> methods = Reflect.findMethods(String.class, "getBytes").toList();

		assertNotNull(methods);
		assertEquals(6, methods.size());
		assertEquals(4, methods.stream().filter(testAgainst(Method::getModifiers, Modifier::isPublic)).count());
		assertTrue(methods.stream().allMatch(against(Method::getName, "getBytes")));
	}

	@Test
	public void testFindMethod() {
		String s = "";
		assertTrue(Reflect.findMethod(String.class, "toString").isPresent());
		assertTrue(Reflect.findMethod(String.class, "toUpperCase", Locale.class).isPresent());
		assertFalse(Reflect.findMethod(String.class, "toUpperCase", Object.class).isPresent());
		assertFalse(Reflect.findMethod(String.class, "toUpperCase", String.class).isPresent());
	}

	@Test
	public void testFindClass() {
		assertTrue(Reflect.findClass("java.lang.String").isPresent());
		assertTrue(Reflect.findClass("java.util.List").isPresent());
		assertFalse(Reflect.findClass("java.xxx.xxx").isPresent());
	}

	@Test
	public void testFindClassWithClassLoader() throws IOException {
		ClassLoader cl = ReflectTest.class.getClassLoader();
		assertTrue(Reflect.findClass("java.lang.String", cl).isPresent());
		assertTrue(Reflect.findClass("java.util.List", cl).isPresent());
		assertFalse(Reflect.findClass("java.xxx.xxx", cl).isPresent());

		ClassLoader cl2 = new ClassLoader() {
			@Override
			protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
				return null;
			}
		};
		assertFalse(Reflect.findClass("java.lang.String", cl2).isPresent());
		assertFalse(Reflect.findClass("java.util.List", cl2).isPresent());
		assertFalse(Reflect.findClass("java.xxx.xxx", cl2).isPresent());
	}

	public record TestInvoke() {
		public String test(String foo) {
			return "Hello " + foo;
		}
	}

	@Test
	public void testFindInvoker() throws IOException {
		TestInvoke inv = new TestInvoke();

		Optional<Reflect.Invoker> invoker = Reflect.findInvoker(TestInvoke.class, "test", String.class);
		assertNotNull(invoker);
		assertTrue(invoker.isPresent());

		String test = invoker.map(i -> (String) i.invoke(inv, "World")).orElse(null);
		assertEquals("Hello World", test);
	}

}
