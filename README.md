# xomda-common-java

Common utility methods, providing more power to streams and functions in general.

## BufferedStream

When streaming in an I/O context, it's not always guaranteed that the receiving end of the Stream will be non-blocking.
For example, when writing to the OutputStream of a slow HTTP connection, will ultimately keep a stream open for too
long.
