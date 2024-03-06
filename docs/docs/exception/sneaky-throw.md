---
outline: deep
---

# SneakyThrow &nbsp;&nbsp;🥷

This sneaky caretaker has to be taken with care. It makes checked exceptions behave like runtime exceptions, by making
them generic. This bypasses the Java compiler and is perfectly allowed within the JVM, which doesn't even know about
checked exceptions.

The trick is done by turning the common java functions such as Consumer, Function, Supplier, ... into throwing ones.
These throwing ones are just sub interfaces of the originals, so the original superclass is then returned. The throwing
method is then caught and the exception is thrown genericly.

```java
Stream.of("a","b","c").

forEach(sneaky(Files::readAllBytes));
```

The problem is now that everything is fine, but you know it may possibly throw an IOException. So in this case, it's
advisable to have your method `throw IOException` too. It hands over resposibility to the caller of your method and
everything is checked again.

It just comes in real handy when working with streams, but always remember that you're just hiding exceptions you would
otherwise had to handle. And it's better to throw these exact exceptions, than to just throw a `RuntimeException(e);`
out of lazyness. It keeps the code clean, but it requires responsibility.
