package org.purely.functions;

@FunctionalInterface
public interface ThrowingSupplier<T> {
   T get() throws Throwable;
}
