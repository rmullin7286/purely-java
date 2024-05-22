package org.purely.collections;

import org.purely.Tuple;
import org.purely.Tuple.Tuple2;
import org.purely.annotations.Pure;
import org.purely.collections.PureLinkedList.Cons;

import java.util.Optional;

@Pure
public final class PureQueue<T> {
   private final PureLinkedList<T> front;
   private final PureLinkedList<T> back;

   public PureQueue() {
       this.front = PureLinkedList.empty();
       this.back = PureLinkedList.empty();
   }

   private PureQueue(PureLinkedList<T> front, PureLinkedList<T> back) {
       this.front = front;
       this.back = back;
   }

   public PureQueue<T> enqueue(T value) {
      return new PureQueue<>(
              front,
              back.prepend(value)
      );
   }

   public Optional<Tuple2<T, PureQueue<T>>> dequeue() {
       final var next = this.prepareFront();
       return switch (next.front) {
           case Cons(var head, var tail) -> Optional.of(Tuple.of(head, new PureQueue<>(tail, back)));
           default -> Optional.empty();
       };
   }

   public Tuple2<T, PureQueue<T>> unsafeDequeue() {
       return dequeue().orElseThrow();
   }

   private PureQueue<T> prepareFront() {
       if(front instanceof Cons<T>) {
           return this;
       }
       return switch (back) {
           case Cons<T> c -> new PureQueue<>(c.reverse(), PureLinkedList.empty());
           default -> this;
       };
   }
}
