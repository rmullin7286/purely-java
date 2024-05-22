package org.purely.internal;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ReverseArrayIterable<T> implements Iterable<T> {
    private final T[] arr;

    public ReverseArrayIterable(T[] arr) {
        this.arr = arr;
    }

    @Override
    public Iterator<T> iterator() {
        return new ReverseIterator();
    }

    private class ReverseIterator implements Iterator<T> {
        private int idx = arr.length - 1;

        @Override
        public boolean hasNext() {
            return idx >= 0;
        }

        @Override
        public T next() {
            if(idx <= 0) {
                throw new NoSuchElementException();
            }
            return arr[idx--];
        }
    }
}
