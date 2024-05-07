package org.purely.functions;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
   R apply(T value) throws Throwable;
}
