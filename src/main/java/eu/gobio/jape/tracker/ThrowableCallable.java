package eu.gobio.jape.tracker;

@FunctionalInterface
public interface ThrowableCallable {
    Object call() throws Throwable;
}
