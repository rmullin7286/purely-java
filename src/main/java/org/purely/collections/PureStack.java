package org.purely.collections;

import org.purely.Tuple;
import org.purely.Tuple.Tuple2;
import org.purely.annotations.Pure;
import org.purely.collections.PureLinkedList.Cons;

import java.util.Optional;

@Pure 
public final class PureStack<T> {
    private final PureLinkedList<T> internal;

    public PureStack() {
       this(PureLinkedList.empty());
    }

    private PureStack(PureLinkedList<T> internal) {
        this.internal = internal;
    }

    public PureStack<T> push(T value) {
        return new PureStack<>(internal.prepend(value));
    }

    public Optional<Tuple2<T, PureStack<T>>> pop() {
        return switch (internal) {
            case Cons(var head, var tail) -> Optional.of(Tuple.of(head, new PureStack<>(tail)));
            default -> Optional.empty();
        };
    }

    public Tuple2<T, PureStack<T>> unsafePop() {
        return pop().orElseThrow();
    }
}
