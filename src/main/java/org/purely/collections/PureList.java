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

    /**
     * Adds all the elements of the given iterable at the specified index. This method may throw
     * {@link IndexOutOfBoundsException} if the specified index is outside the bounds of the list. For a safer
     * alternative, use {@link #addAllOptional(int, Iterable)}.
     *
     * @param index    index at which to insert the first element from the specified iterable.
     * @param elements iterable containing elements to be added to this list.
     * @return A new list containing the elements of the iterable at the specified index.
     * @throws IndexOutOfBoundsException if the index is outside the bounds of the list.
     */
    PureList<T> addAll(int index, Iterable<? extends T> elements);

    /**
     * A safe version of {@link #addAll(int, Iterable)}, that returns {@code Optional.empty()} when the index is out
     * of bounds.
     *
     * @param index    the index at which to insert the first element from the specified collection.
     * @param elements iterable containing elements to be added to this list.
     * @return A new list containing the elements of the iterable at the specified index.
     */
    Optional<? extends PureList<T>> addAllOptional(int index, Iterable<? extends T> elements);

    /**
     * Gets the element at the specified index, or {@code Optional.empty()} if the index is out of bounds.
     *
     * @param index The index of the element to retrieve.
     * @return The element at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of bounds of the list.
     */
    T get(int index);

    Optional<T> getOptional(int index);

    /**
     * Sets the element at the specified index to the new value.
     * <p>
     * This operation may throw {@link IndexOutOfBoundsException}.For a safe alternative, use {@link #setOptional(int, T)}.
     *
     * @param index   index of the element to replace.
     * @param element element to be inserted at the specified position.
     * @return A new list with the element at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    PureList<T> set(int index, T element);

    /**
     * Sets the element at the specified index to the new value.
     *
     * @param index   index of the element to replace.
     * @param element element to be inserted at the specified position.
     * @return A new list with the element at the specified position, or {@code Optional.empty()} if the index was
     * out of bounds
     */
    Optional<? extends PureList<T>> setOptional(int index, T element);

    /**
     * Sets the element at the specified index, and returns a tuple containing the previous element, and the list with
     * the element updated.
     * <p>
     * This operation may throw an {@link IndexOutOfBoundsException}. For a safe alternative, use {@link #getAndSetOptional(int, Object)}
     *
     * @param index   index of the element to replace.
     * @param element element to be inserted at the specified position.
     * @return A tuple containing the previous element, and a list containing the new element.
     * @throws IndexOutOfBoundsException if the index was out of bounds.
     */
    Tuple2<T, ? extends PureList<T>> getAndSet(int index, T element);

    /**
     * Sets the element at the specified index, and returns a tuple containing the previous element and the list with
     * the element updated, or an {@code Optional.empty()} if the index was out of bounds.
     *
     * @param index   The index of the element to replace.
     * @param element element to be inserted at the specified position.
     * @return A tuple containing the previous element, and a list containing the new element.
     */
    Optional<? extends Tuple2<T, ? extends PureList<T>>> getAndSetOptional(int index, T element);

    /**
     * Adds the specified element at the specified position in the list. Shifts the element currently at that position
     * (if any) and subsequent elements to the right.
     * <p>
     * This operation may throw {@link IndexOutOfBoundsException}. For a safe alternative, use {@link #addOptional(int, Object)}
     *
     * @param index index at which the specified element should be inserted.
     * @param value element to be inserted.
     * @return A new list with the element added at the specified position.
     */
    PureList<T> add(int index, T value);

    /**
     * Adds the specified element at the specified position in the list. Shifts the element currently at that position
     * (if any) and subsequent elements to the right. If the index is out of bounds, an {@code Optional.empty()} is returned
     * instead of a new list.
     *
     * @param index index at which the specified element should be inserted.
     * @param value element to be inserted.
     * @return A new list with the element added at the specified position.
     */
    Optional<? extends PureList<T>> addOptional(int index, T value);

    /**
     * Removes the element at the specified index.
     * <p>
     * This operation may throw {@link IndexOutOfBoundsException}. For a safe alternative, use {@link #removeOptional(int)}.
     *
     * @param index The index of the element to remove.
     * @return A new list with the element at the specified index removed.
     */
    PureList<T> remove(int index);

    /**
     * Remove the element at the specified index. If the index is out of bounds, an {@code Optional.empty()} is returned
     * instead of a new list.
     *
     * @param index The index of the element to remove.
     * @return A new list with the element at the specified index removed.
     */
    Optional<? extends PureList<T>> removeOptional(int index);

    Tuple2<T, ? extends PureList<T>> removeAndGet(int index);

    Optional<? extends Tuple2<T, ? extends PureList<T>>> removeAndGetOptional(int index);

    Optional<Integer> indexOf(Object o);

    Optional<Integer> lastIndexOf(Object o);

    ListIterator<T> listIterator();

    Optional<ListIterator<T>> listIterator(int index);

    Optional<? extends PureList<T>> subList(int from, int to);
}
