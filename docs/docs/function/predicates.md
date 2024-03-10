---
outline: deep
---

# Predicates &nbsp;🕵

Predicates is a set of helper methods which facilitate with writing clean functional code.
It's designed to replace common lambda's, using logical naming.

## against

Filter against a property of an object.

This may be helpfull when going through a stream, where you don't want to dig into the object using a lambda.

```java
Predicates.against(Person::getName, "Johnny"); // Predicate<Person>
```

## all

Combine multiple predicates into one, where **each** provided Predicate should evaluate to `true`.

```java
Predicates.all(predicate1, predicate2); // Predicate<T>
```

## any

Combine multiple predicates into one, where **any** of the provided Predicate should evaluate to `true`.

```java
Predicates.any(predicate1, predicate2); // Predicate<T>
```

## always(boolean)

No matter what, this Predicate will always return the given boolean value.

```java
Predicates.always(true); // Predicate<?>
```

## alwaysTrue

No matter which value provided, this Predicate will always return `true`.

```java
Predicates.alwaysTrue(); // Predicate<?>
```

## alwaysFalse

No matter which value provided, this Predicate will always return `false`.

```java
Predicates.alwaysFalse(); // Predicate<?>
```

## cached

Cache the outcome (null-safe) of a Predicate against the distinct values it's provided.
Each consecutive test for a value which has already been encountered, will fetch the result from a cache rather than
evaluating the Predicate again.

This can be helpful when the Predicate houses an expensive operation, which does not change within the lifespan of the
Predicate.

```java
Predicates.cached(Files::exists); // Predicate<Path>
```

## safe

Evaluates the outcome of a Boolean-method as a null-safe predicate.
If the Boolean-method returns `null`, then the Predicate will result to `false`.

```java
Predicates.safe(Method::getBoolean); // Predicate<Method>
```
