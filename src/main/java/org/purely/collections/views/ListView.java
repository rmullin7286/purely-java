package org.purely.collections.views;

import org.purely.collections.PureList;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("unchecked")
public abstract class ListView<T, C extends PureList<T>> extends SequencedCollectionView<T, C> implements List<T> {
    public ListView(C delegate) {
        super(delegate);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        var ret = delegate.get().addAll(c);
        boolean changed = delegate.get().size() == ret.size();
        delegate.set((C) ret);
        return changed;
    }

    @Override
    public T get(int index) {
        return delegate.get().get(index);
    }

    @Override
    public T set(int index, T element) {
        var result = delegate.get().getAndSet(index, element);
        delegate.set((C) result.second());
        return result.first();
    }

    @Override
    public void add(int index, T element) {
        delegate.update(i -> (C) i.add(index, element));
    }

    @Override
    public T remove(int index) {
        var result = delegate.get().removeAndGet(index);
        delegate.set((C) result.second());
        return result.first();
    }

    @Override
    public int indexOf(Object o) {
        return delegate.get().indexOf(o).orElse(-1);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.get().lastIndexOf(o).orElse(-1);
    }

    @Override
    public ListIterator<T> listIterator() {
        return delegate.get().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return delegate.get().listIterator(index).orElseThrow(IndexOutOfBoundsException::new);
    }
}
