package org.purely.internal;

public class MutableRef<T> {
    public T value;

    public MutableRef(T value) {
        this.value = value;
    }
}
