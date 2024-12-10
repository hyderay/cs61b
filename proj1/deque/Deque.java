package deque;

public interface Deque<T> {
    void addFirst(T item);
    void addLast(T item);

    default boolean isEmpty() {
        return size() == 0;
    }

    int size();
    T get(int index);
    void printDeque();
    T removeFirst();
    T removeLast();
    boolean equals(Object o);
}
