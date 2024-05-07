package org.purely.functions;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T value) throws Throwable;
}
