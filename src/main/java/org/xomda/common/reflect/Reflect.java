package org.xomda.common.reflect;

import static org.xomda.common.function.Predicates.against;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.xomda.common.exception.SneakyThrow;

/**
 * Be careful what you do.
 */
public final class Reflect {

	/**
	 * Unchecked cast without warning.
	 */
	public static <P> P unchecked(final Object p) {
		@SuppressWarnings("unchecked")
		final P r = (P) p;
		return r;
	}

	/**
	 * Find a class with a given classname.
	 */
	public static <T> Optional<Class<T>> findClass(final String className) {
		try {
			final Class<T> found = unchecked(Class.forName(className));
			return Optional.of(found);
		} catch (final ClassNotFoundException e) {
			return Optional.empty();
		}
	}

	/**
	 * Find a class with a given classname and class loader and specify whether to initialize the class or not.
	 */
	public static <T> Optional<Class<T>> findClass(final String className, final boolean initialize, final ClassLoader classLoader) {
		try {
			final Class<T> found = unchecked(Class.forName(className, initialize, classLoader));
			return Optional.of(found);
		} catch (final ClassNotFoundException e) {
			return Optional.empty();
		}
	}

	/**
	 * Find a class with a given classname and class loader.
	 */
	public static <T> Optional<Class<T>> findClass(final String className, final ClassLoader classLoader) {
		return findClass(className, true, classLoader);
	}

	/**
	 * Find all methods in given class with a given name.
	 */
	public static Stream<Method> findMethods(final Class<?> clazz, final String name) {
		if (null == name || name.isBlank()) {
			throw new IllegalArgumentException("The name should not be blank or null");
		}
		return Arrays
				.stream(clazz.getDeclaredMethods())
				.filter(against(Method::getName, name));
	}

	/**
	 * Find a method in given class with a given name and arguments.
	 */
	public static Optional<Method> findMethod(final Class<?> clazz, final String name, Class<?>... args) {
		return findMethods(clazz, name)
				.filter(null == args || args.length == 0
						? (Method method) -> method.getParameterCount() == 0
						: (Method method) -> {
							if (method.getParameterCount() < args.length) {
								return false;
							}
							Class<?>[] params = method.getParameterTypes();
							for (int i = 0; i < args.length; i++) {
								if (!Objects.equals(params[i], args[i])) {
									return false;
								}
							}
							return true;
						})
				.findFirst();
	}

	@FunctionalInterface
	public interface Invoker {
		<T, E extends Throwable> T invoke(Object subject, Object... args) throws E;
	}

	/**
	 * Find a method in given class with a given name and arguments.
	 */
	public static Optional<Invoker> findInvoker(final Class<?> clazz, final String name, Class<?>... args) {
		return findMethod(clazz, name, args)
				.map((Method method) -> new Invoker() {
					@Override
					public <T, E extends Throwable> T invoke(Object subject, final Object... args) throws E {
						try {
							return unchecked(method.invoke(subject, args));
						} catch (Exception e) {
							SneakyThrow.throwSneaky(e);
						}
						return null;
					}
				});
	}

	private Reflect() {
	}
}
