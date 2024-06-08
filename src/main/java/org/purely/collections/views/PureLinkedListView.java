package org.purely.collections.views;

import org.purely.collections.PureLinkedList;

import java.util.List;

public class PureLinkedListView<T> extends ListView<T, PureLinkedList<T>> {
    public PureLinkedListView(PureLinkedList<T> delegate) {
        super(delegate);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new PureLinkedListView<>(delegate.get().subList(fromIndex, toIndex).orElseThrow(IndexOutOfBoundsException::new));
    }
}
