---
outline: deep
---

# BufferedStream &nbsp;&nbsp;💨 → 🥞 → 🐌

When streaming in an I/O context for example, it's not always guaranteed that the receiving end of the stream will be
non-blocking.
For example, when writing to the OutputStream of a slow HTTP connection, ultimately the stream which is being consumed
is kept open for too
long.

If the stream that you're consuming is a streaming resultset which keeps open an expensive data connection, then you
want to close that stream as soon as possible. So it's best not to rely on the consuming capacity of the receiving end.
A BufferedOutputStream is also not enough to catch that up.

That's where the BufferedStream comes in. It will consume the entire input stream as soon as the output stream polls for
the first element.
BufferedStream relies on an internal FIFO-stack, onto which the incoming stream is consumed and from which the going
stream is going to grab items at the same time.

Remember that you can always build in multiple BufferedStreams, this way you can go from pile to (cheaper) pile. (💨 →
🍔 → 🥪 → 🥓 → 🐌)

It's possible upon construction to provide a Deque supplier, which will then be used as internal cache.  
There are two built-in default cache implementations.

1. ## Default Cache
   The default cache is just a class extending ConcurrentLinkedDeque. It's thread-safe, that's that.

2. ## Limited Blocking Cache
   This cache extends the default cache, but blocks pushing as long as a maximum allowed number of items is reached.
   It will keep the incoming stream open for a longer amount of time, but it will reduce memory usage.
