package org.purely.collections;

import org.purely.Tuple;
import org.purely.Tuple.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@link PureLinkedList} is a singly linked defined recursively as a link of {@link Cons} objects terminated by
 * a {@link Nil} object. The terminology was borrowed directly from Lisp. Generally, operations that prepend to a
 * {@link PureLinkedList} have the benefit of being able to share the tail with the source list, and can run in constant
 * time and space. Any operation that modifies elements later in the list will have to recreate all elements prior to
 * those later elements. Generally, if you know you have a {@link PureLinkedList}, you should be using operations like
 * {@link #addFirst(Object)}, {@link #getFirst()}, etc.
 * <p>
 * The Linked List is the foundational data structure of functional programming. Unfortunately, there are certain
 * limitations of the Java language that make it less ergonomic.
 * <p>
 * Due to the lack of tail call optimization, it's unsafe to call unbounded recursive operations on A linked list. For
 * example, a natural way to write a contains() function would be;
 * <pre>{@code
 * default boolean contains(Object o) {
 *  return switch(this) {
 *      case Cons(var head, var ignore) when head.equals(o) -> true;
 *      case Cons(var head, var tail) -> tail.contains(o);
 *      default -> false;
 *  }
 * }
 * }</pre>
 * However, doing so would blow up the call stack and likely lead to StackOverflowErrors on any non-trivially sized
 * linked list. As such many of the operations within this class are written in an imperative style, leveraging certain
 * operations like {@link #reversed()} that may be slower than a classical implementation in a language like Haskell or
 * Lisp.
 * <p>
 * The Scala standard library also gets around performance deficiencies by making the tail reference privately mutable,
 * but doing so here would mean that {@link Cons} can't be a record and therefore can't be destructured in pattern matching
 * statements. This may be solved by a future JEP that proposes adding destructors to classes for pattern matching purposes,
 * but as of Java 21 that's not possible.
 *
 * @param <T> The type contained by the {@link PureLinkedList}
 */
public sealed interface PureLinkedList<T> extends PureList<T> {
    /**
     * {@inheritDoc}
     * <p>
     * runtime and space complexity: O(1)
     */
    static <T> PureLinkedList<T> empty() {
        return Nil.instance();
    }

    /**
     * {@inheritDoc}
     * <p>
     * runtime and space complexity: O(N)
     */
    @Override
    default PureLinkedList<T> reversed() {
        PureLinkedList<T> ret = PureLinkedList.empty();
        for (T t : this) {
            ret = ret.addFirst(t);
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>
     * runtime and space complexity: O(1)
     */
    @Override
    default PureLinkedList<T> addFirst(T t) {
        return new Cons<>(t, this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * runtime and space complexity: O(N)
     */
    @Override
    default PureLinkedList<T> addLast(T t) {
        return this.reversed().addFirst(t).reversed();
    }

    /**
     * {@inheritDoc}
     * <p>
     * runtime and space complexity: O(1)
     */
    @Override
    default Optional<T> getFirst() {
        return switch (this) {
            case Cons<T> c -> Optional.of(c.head());
            case Nil<T> ignore -> Optional.empty();
        };
    }

    /**
     * {@inheritDoc}
     * <p>
     * runtime complexity: O(N)
     * space complexity: O(1)
     */
    @Override
    default Optional<T> getLast() {
        if (this.isEmpty()) {
            return Optional.empty();
        }

        Cons<T> c = (Cons<T>) this;
        while (c.tail() instanceof Cons<T> t) {
            c = t;
        }

        return Optional.of(c.head());
    }

    @Override
    default List<T> toMutable() {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * {@inheritDoc}
     * <p>
     * runtime complexity: O(N)
     * space complexity: O(1)
     */
    @Override
    default int size() {
        int ret = 0;
        for (T ignore : this) {
            ret++;
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>
     * runtime and space complexity: O(1)
     */
    @Override
    default boolean isEmpty() {
        return this instanceof Nil<T>;
    }

    /**
     * {@inheritDoc}
     * <p>
     * runtime complexity: O(N)
     * space complexity: O(1)
     */
    @Override
    default boolean contains(Object o) {
        for (T t : this) {
            if (t.equals(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * runtime and space complexity: O(N)
     */
    @SuppressWarnings("unchecked")
    @Override
    default T[] toArray() {
        return (T[]) this.stream().toArray();
    }

    /**
     * {@inheritDoc}
     * <p>
     * runtime and space complexity: O(N)
     */
    @Override
    default PureLinkedList<T> add(T t) {
        return this.addLast(t);
    }

    @Override
    default PureLinkedList<T> remove(Object o) {
        PureLinkedList<T> front = Nil.instance();
        PureLinkedList<T> back = this;
        while (back instanceof Cons(var head, var tail)) {
            back = tail;
            if (head.equals(o)) {
                break;
            }
            front = front.addFirst(head);
        }
        for (T t : front) {
            back = back.addFirst(t);
        }
        return back;
    }

    @Override
    default PureLinkedList<T> addAll(Iterable<? extends T> i) {
        var ret = this.reversed();
        for (T t : i) {
            ret = ret.addFirst(t);
        }
        return ret.reversed();
    }

    @Override
    default PureLinkedList<T> removeAll(Iterable<?> i) {
        var ret = this;
        for (var t : i) {
            ret = ret.remove(t);
        }
        return ret;
    }

    @Override
    default PureLinkedList<T> retainAll(Iterable<?> i) {
        PureLinkedList<T> ret = Nil.instance();
        for (T t : this) {
            for (Object o : i) {
                if (t.equals(o)) {
                    ret = ret.addFirst(t);
                }
            }
        }
        return ret.reversed();
    }

    @Override
    default PureLinkedList<T> clear() {
        return PureLinkedList.empty();
    }

    @Override
    default Iterator<T> iterator() {
        return new Iterator<>() {
            PureLinkedList<T> cur = PureLinkedList.this;

            @Override
            public boolean hasNext() {
                return cur instanceof Cons<T>;
            }

            @Override
            public T next() {
                return switch (cur) {
                    case Cons(var head, var tail) -> {
                        cur = tail;
                        yield head;
                    }
                    default -> throw new NoSuchElementException();
                };
            }
        };
    }

    @Override
    default Optional<Tuple2<T, PureLinkedList<T>>> removeFirst() {
        return switch (this) {
            case Cons(var head, var tail) -> Optional.of(Tuple.of(head, tail));
            default -> Optional.empty();
        };
    }

    @Override
    default Optional<Tuple2<T, PureLinkedList<T>>> removeLast() {
        return this.reversed().removeFirst().map(i -> Tuple.of(i.first(), i.second().reversed()));
    }

    @Override
    default Optional<PureLinkedList<T>> addAll(int index, Iterable<? extends T> element) {
        PureLinkedList<T> front = PureLinkedList.empty();
        PureLinkedList<T> rear = this;
        var curIdx = index;
        while (curIdx > 0 && rear instanceof Cons(var head, var tail)) {
            curIdx--;
            front = front.addFirst(head);
            rear = tail;
        }
        if (curIdx != 0) {
            return Optional.empty();
        }
        for (T t : element) {
            front = front.addFirst(t);
        }
        while (front instanceof Cons(var head, var tail)) {
            front = tail;
            rear = rear.addFirst(head);
        }
        return Optional.of(rear);
    }

    @Override
    default Optional<T> get(int index) {
        var curIdx = index;
        for (T t : this) {
            if (curIdx == 0) {
                return Optional.of(t);
            }
            curIdx--;
        }

        return Optional.empty();
    }

    @Override
    default Optional<Tuple2<T, PureLinkedList<T>>> set(int index, T element) {
        PureLinkedList<T> front = PureLinkedList.empty();
        PureLinkedList<T> rear = this;
        var curIdx = index;
        while (curIdx > 0 && rear instanceof Cons(var head, PureLinkedList<T> tail)) {
            front = front.addFirst(head);
            rear = tail;
            curIdx--;
        }
        if (curIdx != 0) {
            return Optional.empty();
        }
        if (rear instanceof Cons(var head, var tail)) {
            rear = new Cons<>(element, tail);
            for (T t : front) {
                rear = rear.addFirst(t);
            }
            return Optional.of(Tuple.of(head, rear));
        }
        return Optional.empty();
    }

    @Override
    default Optional<PureLinkedList<T>> add(int index, T value) {
        PureLinkedList<T> front = PureLinkedList.empty();
        PureLinkedList<T> rear = this;
        int curIdx = index;
        while (curIdx > 0 && rear instanceof Cons(var head, var tail)) {
            curIdx--;
            front = front.addFirst(head);
            rear = tail;
        }
        if (curIdx != 0) {
            return Optional.empty();
        }
        rear = new Cons<>(value, rear);
        for (T t : front) {
            rear = rear.addFirst(t);
        }
        return Optional.of(rear);
    }

    @Override
    default Optional<Tuple2<T, PureLinkedList<T>>> remove(int index) {
        PureLinkedList<T> front = PureLinkedList.empty();
        PureLinkedList<T> rear = this;
        var curIdx = index;
        while (curIdx > 0 && rear instanceof Cons(var head, var tail)) {
            curIdx--;
            front = front.addFirst(head);
            rear = tail;
        }
        if (curIdx != 0) {
            return Optional.empty();
        }
        if (rear instanceof Cons(var head, var tail)) {
            rear = tail;
            for (T t : front) {
                rear = rear.addFirst(t);
            }
            return Optional.of(Tuple.of(head, rear));
        }
        return Optional.empty();
    }

    @Override
    default Optional<Integer> indexOf(Object o) {
        int idx = 0;
        for (T t : this) {
            if (t.equals(o)) {
                return Optional.of(idx);
            }
            idx++;
        }
        return Optional.empty();
    }

    @Override
    default Optional<Integer> lastIndexOf(Object o) {
        Optional<Integer> ret = Optional.empty();
        int idx = 0;
        for (T t : this) {
            if (t.equals(o)) {
                ret = Optional.of(idx);
            }
            idx++;
        }
        return ret;
    }

    @Override
    default ListIterator<T> listIterator() {
        return new MyListIterator<>(PureLinkedList.empty(), this, 0);
    }

    @Override
    default Optional<ListIterator<T>> listIterator(int index) {
        var it = new MyListIterator<>(PureLinkedList.empty(), this, 0);
        for (int i = 0; i < index; i++) {
            if (!it.hasNext()) {
                return Optional.empty();
            }
            it.next();
        }
        return Optional.of(it);
    }

    @Override
    default Optional<PureLinkedList<T>> subList(int from, int to) {
        var cur = this;
        int curFrom = from;
        while (curFrom >= 0 && cur instanceof Cons(var head, var tail)) {
            cur = tail;
            curFrom--;
        }
        if (curFrom != 0) {
            return Optional.empty();
        }
        PureLinkedList<T> ret = PureLinkedList.empty();
        int curTo = to - 1;
        while (curTo >= 0 && cur instanceof Cons(var head, var tail)) {
            ret = ret.addFirst(head);
        }
        if (curTo != 0) {
            return Optional.empty();
        }
        return Optional.of(ret.reversed());

    }

    record Cons<T>(T head, PureLinkedList<T> tail) implements PureLinkedList<T> {
        public Cons {
            Objects.requireNonNull(head, "head cannot be null");
            Objects.requireNonNull(tail, "tail cannot be null");
        }

        @Override
        public String toString() {
            return "[" + this.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
        }
    }

    final class Nil<T> implements PureLinkedList<T> {
        private static final Nil<?> INSTANCE = new Nil<>();

        private Nil() {
            // singleton
        }

        @SuppressWarnings("unchecked")
        public static <T> Nil<T> instance() {
            return (Nil<T>) INSTANCE;
        }

        @Override
        public String toString() {
            return "[]";
        }
    }

    class MyListIterator<T> implements ListIterator<T> {
        private PureLinkedList<T> front;
        private PureLinkedList<T> rear;
        private int idx;

        public MyListIterator(PureLinkedList<T> front, PureLinkedList<T> rear, int index) {
            this.front = front;
            this.rear = rear;
            this.idx = index;
        }

        @Override
        public boolean hasNext() {
            return rear instanceof Cons<T>;
        }

        @Override
        public T next() {
            return switch (rear) {
                case Cons(var head, var tail) -> {
                    front = front.addFirst(head);
                    rear = tail;
                    idx++;
                    yield head;
                }
                default -> throw new NoSuchElementException();
            };
        }

        @Override
        public boolean hasPrevious() {
            return front instanceof Cons<T>;
        }

        @Override
        public T previous() {
            return switch (front) {
                case Cons(var head, var tail) -> {
                    front = tail;
                    rear = rear.addFirst(head);
                    idx--;
                    yield head;
                }
                default -> throw new NoSuchElementException();
            };
        }

        @Override
        public int nextIndex() {
            return idx + 1;
        }

        @Override
        public int previousIndex() {
            return idx - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(T t) {
            throw new UnsupportedOperationException();
        }
    }
}
