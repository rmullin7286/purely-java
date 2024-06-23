package org.purely.collections;

import org.junit.jupiter.api.Test;
import org.purely.Tuple;
import org.purely.collections.PureLinkedList.Cons;
import org.purely.collections.PureLinkedList.Nil;
import org.purely.collections.views.PureLinkedListView;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PureLinkedListTest {

    @Test
    void from() {
        assertEquals(PureLinkedList.of(1, 2, 3), PureLinkedList.from(List.of(1, 2, 3)));
        var original = PureLinkedList.of(1, 2, 3);
        assertSame(original, PureLinkedList.from(original.toMutable()));
    }

    @Test
    void of() {
        assertEquals(new Cons<>(1, new Cons<>(2, new Cons<>(3, Nil.instance()))), PureLinkedList.of(1, 2, 3));
    }

    @Test
    void empty() {
        assertSame(Nil.instance(), PureLinkedList.empty());
    }

    @Test
    void reversed() {
        assertEquals(PureLinkedList.of(3, 2, 1), PureLinkedList.of(1, 2, 3).reversed());
        assertSame(Nil.instance(), PureLinkedList.empty().reversed());
    }

    @Test
    void addFirst() {
        assertEquals(PureLinkedList.of(4, 1, 2, 3), PureLinkedList.of(1, 2, 3).addFirst(4));
    }

    @Test
    void addLast() {
        assertEquals(PureLinkedList.of(1, 2, 3, 4), PureLinkedList.of(1, 2, 3).addLast(4));
    }

    @Test
    void getFirst() {
        assertEquals(Optional.of(1), PureLinkedList.of(1, 2, 3).getFirst());
        assertEquals(Optional.empty(), PureLinkedList.empty().getFirst());
    }

    @Test
    void getLast() {
        assertEquals(Optional.of(3), PureLinkedList.of(1, 2, 3).getLast());
    }

    @Test
    void toMutable() {
        var mut = PureLinkedList.of(1, 2, 3).toMutable();
        assertInstanceOf(PureLinkedListView.class, mut);
    }

    @Test
    void size() {
        assertEquals(3, PureLinkedList.of(1, 2, 3).size());
        assertEquals(0, Nil.instance().size());
    }

    @Test
    void isEmpty() {
        assertFalse(PureLinkedList.of(1, 2, 3).isEmpty());
        assertTrue(PureLinkedList.empty().isEmpty());
    }

    @Test
    void contains() {
        assertTrue(PureLinkedList.of(1, 2, 3).contains(2));
        assertFalse(PureLinkedList.of(1, 2, 3).contains(4));
        assertFalse(Nil.instance().contains(2));
    }

    @Test
    void toArray() {
    }

    @Test
    void add() {
        assertEquals(PureLinkedList.of(1, 2, 3, 4), PureLinkedList.of(1, 2, 3).add(4));
    }

    @Test
    void remove() {
        assertEquals(PureLinkedList.of(1, 3), PureLinkedList.of(1, 2, 3).remove(1));
    }

    @Test
    void addAll() {
        assertEquals(PureLinkedList.of(1, 2, 3, 4, 5, 6), PureLinkedList.of(1, 2, 3).addAll(List.of(4, 5, 6)));
    }

    @Test
    void removeAll() {
        assertEquals(PureLinkedList.of(1), PureLinkedList.of(1, 2, 3).removeAll(List.of(2, 3)));
    }

    @Test
    void retainAll() {
        assertEquals(PureLinkedList.of(1, 3), PureLinkedList.of(1, 2, 3).retainAll(List.of(1, 3)));
    }

    @Test
    void clear() {
        assertSame(Nil.instance(), PureLinkedList.of(1, 2, 3).clear());
    }

    @Test
    void iterator() {
        var iter = PureLinkedList.of(1, 2, 3).iterator();
        assertEquals(1, iter.next());
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());
        assertThrows(NoSuchElementException.class, iter::next);
    }

    @Test
    void removeFirst() {
        assertEquals(PureLinkedList.of(2, 3), PureLinkedList.of(1, 2, 3).removeFirst());
    }

    @Test
    void removeLast() {
        assertEquals(PureLinkedList.of(1, 2), PureLinkedList.of(1, 2, 3).removeLast());
    }

    @Test
    void addAllIndexed() {
        assertEquals(PureLinkedList.of(1, 2, 3, 4), PureLinkedList.of(1, 4).addAll(1, PureLinkedList.of(2, 3)));
        assertEquals(PureLinkedList.of(1, 2, 3, 4), PureLinkedList.of(1, 2).addAll(2, PureLinkedList.of(3, 4)));
        assertThrows(IndexOutOfBoundsException.class, () -> PureLinkedList.of(1, 2, 3).addAll(-1, List.of()));
        assertThrows(IndexOutOfBoundsException.class, () -> PureLinkedList.of(1, 2, 3).addAll(5, List.of()));
    }

    @Test
    void addAllOptionalOptional() {
        assertEquals(Optional.of(PureLinkedList.of(1, 2, 3, 4)), PureLinkedList.of(1, 4).addAllOptional(1, PureLinkedList.of(2, 3)));
        assertEquals(Optional.of(PureLinkedList.of(1, 2, 3, 4)), PureLinkedList.of(1, 2).addAllOptional(2, PureLinkedList.of(3, 4)));
        assertEquals(Optional.empty(), PureLinkedList.of(1, 2, 3).addAllOptional(-1, List.of()));
        assertEquals(Optional.empty(), PureLinkedList.of(1, 2, 3).addAllOptional(5, List.of()));
    }

    @Test
    void get() {
        var list = PureLinkedList.of(1, 2, 3);
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
    }

    @Test
    void getOptional() {
        var list = PureLinkedList.of(1, 2, 3);
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertEquals(Optional.empty(), list.getOptional(-1));
        assertEquals(Optional.empty(), list.getOptional(3));
    }

    @Test
    void set() {
        var list = PureLinkedList.of(1, 2, 3);
        assertEquals(PureLinkedList.of(1, 5, 3), list.set(1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(4, 5));
    }

    @Test
    void setOptional() {
        var list = PureLinkedList.of(1, 2, 3);
        assertEquals(PureLinkedList.of(1, 5, 3), list.set(1, 5));
        assertEquals(Optional.empty(), list.setOptional(-1, 5));
        assertEquals(Optional.empty(), list.setOptional(4, 5));
    }

    @Test
    void getAndSet() {
        var list = PureLinkedList.of(1, 2, 3);
        assertEquals(Tuple.of(2, PureLinkedList.of(1, 5, 3)), list.getAndSet(1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.getAndSet(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.getAndSet(3, 5));
    }

    @Test
    void getAndSetOptional() {
        var list = PureLinkedList.of(1, 2, 3);
        assertEquals(Optional.of(Tuple.of(2, PureLinkedList.of(1, 5, 3))), list.getAndSetOptional(1, 5));
        assertEquals(Optional.empty(), list.getAndSetOptional(-1, 5));
        assertEquals(Optional.empty(), list.getAndSetOptional(3, 5));
    }

    @Test
    void addIndexed() {
        var list = PureLinkedList.of(1, 2, 3);
        assertEquals(PureLinkedList.of(1, 2, 3, 4), list.add(3, 4));
        assertEquals(PureLinkedList.of(1, 4, 2, 3), list.add(1, 4));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(4, 4));
    }

    @Test
    void addIndexedOptional() {
        var list = PureLinkedList.of(1, 2, 3);
        assertEquals(Optional.of(PureLinkedList.of(1, 2, 3, 4)), list.addOptional(3, 4));
        assertEquals(Optional.of(PureLinkedList.of(1, 4, 2, 3)), list.addOptional(1, 4));
        assertEquals(Optional.empty(), list.addOptional(-1, 4));
        assertEquals(Optional.empty(), list.addOptional(4, 4));
    }

    @Test
    void removeIndexed() {

    }

    @Test
    void removeOptional() {
    }

    @Test
    void removeAndGet() {
    }

    @Test
    void removeAndGetOptional() {
    }

    @Test
    void indexOf() {
    }

    @Test
    void lastIndexOf() {
    }

    @Test
    void listIterator() {
    }

    @Test
    void testListIterator() {
    }

    @Test
    void subList() {
    }


    @Test
    void removeFirstOptional() {
    }

    @Test
    void getAndRemoveFirst() {
    }

    @Test
    void getAndRemoveFirstOptional() {
    }

    @Test
    void removeLastOptional() {
    }

    @Test
    void getAndRemoveLast() {
    }

    @Test
    void getAndRemoveLastOptional() {
    }
}