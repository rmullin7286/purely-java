package org.purely.internal;

public final class Arrays {
    private Arrays() {
    }

    public static <T> Iterable<T> iterateReversed(T[] arr) {
        return new ReverseArrayIterable<>(arr);
    }
}
