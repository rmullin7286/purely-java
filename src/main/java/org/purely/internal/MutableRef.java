package org.purely.internal;

import java.util.function.Function;

public class MutableRef<T> {
    private T value;

    public MutableRef(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void update(Function<T, T> f) {
        set(f.apply(get()));
    }
}
