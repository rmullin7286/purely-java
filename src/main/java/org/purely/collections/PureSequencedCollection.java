package org.purely.collections;

import org.purely.Tuple.Tuple2;
import org.purely.annotations.Pure;

import java.util.Optional;

/**
 * The persistent collection hierarchy analogue to {@link java.util.SequencedCollection}.
 */
@Pure
public interface PureSequencedCollection<T> extends PureCollection<T> {
    /**
     * Returns a new {@link PureSequencedCollection} in reverse order of this {@link PureSequencedCollection}.
     * <p>
     * Implementors of this method should specialize the return type to that of the implementing class.
     *
     * @return a new {@link PureSequencedCollection} in reverse order of this {@link PureSequencedCollection}.
     */
    PureSequencedCollection<T> reversed();

    /**
     * Returns a new {@link PureSequencedCollection} with the value prepended.
     *
     * @param t the value to prepend.
     * @return a new {@link PureSequencedCollection} with the value prepended.
     */
    PureSequencedCollection<T> addFirst(T t);

    /**
     * Returns a new {@link PureSequencedCollection} with the value appended.
     *
     * @param t the valuue to append.
     * @return a new {@link PureSequencedCollection} with the value appended.
     */
    PureSequencedCollection<T> addLast(T t);

    /**
     * Gets the first element of the collection if not empty. Otherwise, returns {@code Optional.empty()}.
     *
     * @return the first element of the collection if not empty.
     */
    Optional<T> getFirst();

    /**
     * Gets the last element of the collection if not empty. Otherwise, returns {@code Optional.empty()}
     *
     * @return the last element of the collection if not empty.
     */
    Optional<T> getLast();

    Optional<? extends Tuple2<T, ? extends PureSequencedCollection<T>>> removeFirst();

    Optional<? extends Tuple2<T, ? extends PureSequencedCollection<T>>> removeLast();
}
