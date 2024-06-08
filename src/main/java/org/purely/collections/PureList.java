package org.purely.collections;

import org.purely.Tuple.Tuple2;
import org.purely.annotations.Pure;

import java.util.ListIterator;
import java.util.Optional;

/**
 * The persistent analogue to {@link java.util.List} in the {@link PureCollection} hierarchy.
 */
@Pure
public interface PureList<T> extends PureSequencedCollection<T> {
    Optional<? extends PureList<T>> addAll(int index, Iterable<? extends T> element);

    Optional<T> get(int index);

    Optional<? extends Tuple2<T, ? extends PureList<T>>> set(int index, T element);

    Optional<? extends PureList<T>> add(int index, T value);

    Optional<? extends Tuple2<T, ? extends PureList<T>>> remove(int index);

    Optional<Integer> indexOf(Object o);

    Optional<Integer> lastIndexOf(Object o);

    ListIterator<T> listIterator();

    Optional<ListIterator<T>> listIterator(int index);

    Optional<? extends PureList<T>> subList(int from, int to);
}
