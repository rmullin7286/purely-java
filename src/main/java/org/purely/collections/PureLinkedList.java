package org.purely.collections;

import org.purely.annotations.Pure;
import org.purely.internal.ReverseArrayIterable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Pure
public sealed interface PureLinkedList<T> extends Iterable<T> {
    @SafeVarargs
    static <T> PureLinkedList<T> of(T... values) {
        PureLinkedList<T> ret = Nil.instance();
        for (T t : new ReverseArrayIterable<>(values)) {
            ret = new Cons<>(t, ret);
        }
        return ret;
    }

    static <T> PureLinkedList<T> empty() {
        return Nil.instance();
    }

    default PureLinkedList<T> prepend(T value) {
        return new Cons<>(value, this);
    }

    default PureLinkedList<T> append(T value) {
        return this.reverse().prepend(value).reverse();
    }

    default PureLinkedList<T> reverse() {
        PureLinkedList<T> ret = Nil.instance();
        var cur = this;
        while(cur instanceof Cons(var head, var tail)) {
            ret = new Cons<>(head, ret);
            cur = tail;
        }
        return ret;
    }

    default Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
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
                    default -> throw new NoSuchElementException("PureLinkedList iterator has no more elements.");
                };
            }
        };
    }

    record Cons<T>(T value, PureLinkedList<T> tail) implements PureLinkedList<T> {
    }

    final class Nil<T> implements PureLinkedList<T> {
        private static final Nil<?> INSTANCE = new Nil<>();

        @SuppressWarnings("unchecked")
        public static <T> Nil<T> instance() {
            return (Nil<T>) INSTANCE;
        }
    }
}
