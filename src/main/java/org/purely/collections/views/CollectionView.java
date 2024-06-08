package org.purely.collections.views;

import org.purely.collections.PureCollection;
import org.purely.internal.MutableRef;

import java.util.Collection;
import java.util.Iterator;

public abstract class CollectionView<T, C extends PureCollection<T>> implements Collection<T> {
    protected final MutableRef<C> delegate;

    public CollectionView(C delegate) {
        this.delegate = new MutableRef<>(delegate);
    }

    @Override
    public int size() {
        return delegate.get().size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.get().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.get().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.get().iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.get().toArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1> T1[] toArray(T1[] a) {
        return (T1[]) delegate.get().toArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean add(T t) {
        var res = delegate.get().add(t);
        var changed = res.size() == delegate.get().size();
        delegate.set((C) res);
        return changed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        var res = delegate.get().remove(o);
        var changed = res.size() == delegate.get().size();
        delegate.set((C) res);
        return changed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.get().containsAll(c);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addAll(Collection<? extends T> c) {
        var res = delegate.get().addAll(c);
        var changed = res.size() == delegate.get().size();
        delegate.set((C) res);
        return changed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeAll(Collection<?> c) {
        var res = delegate.get().removeAll(c);
        var changed = res.size() == delegate.get().size();
        delegate.set((C) res);
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        var res = delegate.get().retainAll(c);
        var changed = res.size() == delegate.get().size();
        delegate.set((C) res);
        return changed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        delegate.update(d -> (C) d.clear());
    }
}
