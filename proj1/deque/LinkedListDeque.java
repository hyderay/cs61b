package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node<T> {
        private Node prev;
        private T item;
        private Node next;

        Node(Node prev, T item, Node next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    private Node<T> sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        if (isEmpty()) {
            Node<T> newNode = new Node<>(sentinel, item, sentinel);
            sentinel.next = newNode;
            sentinel.prev = newNode;
        } else {
            Node<T> currFirst = sentinel.next;
            Node<T> newNode = new Node<>(sentinel, item, sentinel.next);
            currFirst.prev = newNode;
            sentinel.next = newNode;
        }
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (isEmpty()) {
            Node<T> newNode = new Node<>(sentinel, item, sentinel);
            sentinel.next = newNode;
            sentinel.prev = newNode;
        } else {
            Node<T> currLast = sentinel.prev;
            Node<T> newNode = new Node<>(sentinel.prev, item, sentinel);
            currLast.next = newNode;
            sentinel.prev = newNode;
        }
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node<T> currentNode;
        if (index < size / 2) {
            currentNode = sentinel.next;
            for (int i = 0; i < index; i++) {
                currentNode = currentNode.next;
            }
        } else {
            currentNode = sentinel.prev;
            for (int i = size - 1; i > index; i--) {
                currentNode = currentNode.prev;
            }
        }
        return currentNode.item;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            T t = get(i);
            System.out.print(t + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node<T> firstNode = sentinel.next;
        T first = firstNode.item;
        Node<T> second = sentinel.next.next;
        sentinel.next = second;
        second.prev = sentinel;
        size -= 1;
        return first;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node<T> lastNode = sentinel.prev;
        T last = lastNode.item;
        Node<T> lastPrev = sentinel.prev.prev;
        sentinel.prev = lastPrev;
        lastPrev.next = sentinel;
        size -= 1;
        return last;
    }

    public T getRecursive(int index) {
        if (isEmpty()) {
            return null;
        }
        Node<T> start = sentinel.next;
        T t = getRecursiveHelper(start, index).item;
        return t;
    }

    private Node<T> getRecursiveHelper(Node<T> t, int index) {
        if (index == 0) {
            return t;
        }
        return getRecursiveHelper(t.next, index - 1);
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int wizPos;

        LinkedListDequeIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size();
        }

        public T next() {
            T returnItem = get(wizPos);
            wizPos++;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Deque<?>) {
            Deque<?> sets = (Deque<?>) o;
            if (size() != sets.size()) {
                return false;
            }
            for (int i = 0; i < size(); i++) {
                Object item = sets.get(i);
                if (!item.equals(this.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
