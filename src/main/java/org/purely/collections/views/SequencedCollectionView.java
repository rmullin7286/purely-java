package org.purely.collections.views;

import org.purely.collections.PureSequencedCollection;

import java.util.SequencedCollection;

public abstract class SequencedCollectionView<T, C extends PureSequencedCollection<T>> extends CollectionView<T, C> implements SequencedCollection<T> {
    public SequencedCollectionView(C delegate) {
        super(delegate);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addFirst(T t) {
        delegate.update(i -> (C) i.addFirst(t));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addLast(T t) {
        delegate.update(i -> (C) i.addFirst(t));
    }

    @Override
    public T getFirst() {
        return delegate.get().getFirst().orElseThrow();
    }

    @Override
    public T getLast() {
        return delegate.get().getLast().orElseThrow();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T removeFirst() {
        return delegate.get().removeFirst()
                .map(i -> {
                    delegate.set((C) i.second());
                    return i.first();
                }).orElseThrow();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T removeLast() {
        return delegate.get().removeLast()
                .map(i -> {
                    delegate.set((C) i.second());
                    return i.first();
                }).orElseThrow();
    }
}
