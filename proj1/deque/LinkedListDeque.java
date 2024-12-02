package deque;

public class LinkedListDeque<T> {
    private class Node<T> {
        private Node prev;
        private T item;
        private Node next;

        public Node(Node prev, T item, Node next) {
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

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public T get(int index) {
        if (isEmpty()) {
            return null;
        }
        Node<T> newNode = sentinel.next;
        for (int i = 0; i < index; i++) {
           newNode = newNode.next;
           if (newNode.item == null) {
               return null;
           }
       }
       return newNode.item;
    }

    public void printDeque() {
        for (int i = 0; i < size; i++) {
            T t = this.get(i);
            System.out.print(t + " ");
        }
        System.out.println();
    }

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

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T last = this.get(size-1);
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

    public Node<T> getRecursiveHelper(Node<T> t, int index) {
        if (index == 0) {
            return t;
        }
        return getRecursiveHelper(t.next, index-1);
    }
}