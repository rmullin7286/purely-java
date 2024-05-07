package org.purely.functions;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
   R apply(T value) throws Throwable;

   static <T, R> ThrowingFunction<T, R> wrap(Function<T, R> function) {
      return function::apply;
   }
}
