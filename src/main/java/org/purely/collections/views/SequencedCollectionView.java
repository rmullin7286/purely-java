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
        var ret = delegate.get().getAndRemoveFirst();
        delegate.set((C)ret.second());
        return ret.first();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T removeLast() {
        var ret = delegate.get().getAndRemoveLast();
        delegate.set((C)ret.second());
        return ret.first();
    }
}
