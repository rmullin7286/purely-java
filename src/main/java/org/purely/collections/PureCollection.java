package org.purely.collections;

import org.purely.annotations.Pure;

import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The base of the persistent collection class hierarchy. This interface serves as the persistent analogue to
 * {@link java.util.Collection}, however unlike other libraries this interface does not implement {@link java.util.Collection}.
 * This is done to ensure correctness of operations, as calling any mutating operation from {@link java.util.Collection}
 * would have to throw a {@link UnsupportedOperationException}. Instead, this interface and each of it's implementing classes
 * will implement a pair of methods, {@code toMutable()} and {@code fromMutable()} to interop with apis that expect standard
 * Java collections. These methods are designed to be efficient, making no copies in the case of {@code toMutable()} but
 * instead returning "views" containing mutable references to the pure implementation. {@code fromMutable()} may also
 * be able to optimize if the received collection was created using {@code toMutable()}. So The operation
 * <pre>{@code
 *  PureLinkedList.fromMutable( myList.toMutable() );
 * }</pre>
 * is guaranteed to be O(1) in space and time.
 * <p>
 * Mutable operations from the standard {@link java.util.Collection} interface and it's implementors will instead return
 * a new instance of the class rather than modifying the original object. This is made efficient by the fact that since
 * we know that no PureCollection could every be modified in place, we can share data from the original value with
 * the new value.
 * <p>
 * All operations on implementing interfaces should explain their O(n) runtime complexity.
 */
@Pure
public interface PureCollection<T> extends Iterable<T> {

    /**
     * Converts this PureCollection into a {@link Collection}, implementing all mutability methods fully.
     * This method is guaranteed to be O(1) in time.
     * <p>
     * implementors of this method should specialize the return type to the analogous type in the {@link Collection}
     * hierarchy.
     *
     * @return a {@link Collection} view for the given {@link PureCollection}
     */
    Collection<T> toMutable();

    /**
     * Returns the number of elements in this collection.
     *
     * @return the number of elements in this collection.
     */
    int size();

    /**
     * Returns {@code true} if this collection contains no elements.
     *
     * @return {@code true} if this collection contains no elements.
     */
    boolean isEmpty();

    /**
     * returns {@code true} if this collection contains the specified element.
     *
     * @param o element whose presence in the collection is to be tested.
     * @return {@code true} if this collection contains the specified element.
     */
    boolean contains(Object o);

    /**
     * Returns an array containing all the elements in this collection.
     *
     * @return an array containing all the elements in this collection.
     */
    T[] toArray();

    /**
     * Returns a new {@link PureCollection} containing the specified element.
     * <p>
     * Implementors of this method should specialize the return type to the implementing class's type.
     *
     * @param t the element to add.
     * @return a new {@link PureCollection}
     */
    PureCollection<T> add(T t);

    /**
     * Returns a new {@link PureCollection} with a single instance of the specified element removed, if present.
     * <p>
     * Implementors of this method should specialize the return type to the implementing class's type.
     *
     * @param o element to be removed if present.
     * @return a new {@link PureCollection} with a single instance of the specified element removed, if present.
     */
    PureCollection<T> remove(Object o);

    /**
     * Returns true if this collection contains all elements of the given iterable.
     * <p>
     * The default implementation is O(NxM) in time and O(1) in space,
     * where N is the size of the input, and M is the size of this collection.
     *
     * @param i iterable to be checked for containment in this collection.
     * @return true if this collection contains all the elements in the specified collection.
     */
    default boolean containsAll(Iterable<?> i) {
        return StreamSupport.stream(i.spliterator(), false)
                .allMatch(this::contains);
    }

    /**
     * Creates a new PureCollection with all the elements of the {@link Iterable} added.
     * <p>
     * Implementors of this method should specialize the return type to the implementing class's type.
     *
     * @param i elements to add.
     * @return a new PureCollection with all the elements of the {@link Iterable} added.
     */
    PureCollection<T> addAll(Iterable<? extends T> i);

    /**
     * Creates a new PureCollection with all the elements of the {@link Iterable} removed.
     * <p>
     * Implementors of this method should specialize the return type to the implementing class's type.
     *
     * @param i elements to add.
     * @return a new PureCollection with all the elements of the {@link Iterable} removed.
     */
    PureCollection<T> removeAll(Iterable<?> i);

    /**
     * Creates a new PureCollection containing only the elements specified by the argument.
     *
     * @param i An iterable containing elements that should be retained in the new PureCollection.
     * @return a new PureCollection containing only the elements also contained by the iterable.
     */
    PureCollection<T> retainAll(Iterable<?> i);

    /**
     * Returns the empty value for this collection.
     * <p>
     * Implementors of this method should specialize the return type to the implementing class's type.
     *
     * @return The empty value for this collection.
     */
    PureCollection<T> clear();

    /**
     * Returns a sequential {@link Stream} with this collection as its source.
     *
     * @return a sequential {@link Stream} over the elements in this collection.
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
