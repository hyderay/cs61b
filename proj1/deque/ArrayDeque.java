package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] lst;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        lst = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    private void resize(int capacity) {
        T[] newLst = (T[]) new Object[capacity];
        int newFirst = (int) Math.round(capacity * 0.25);
        for (int i = 0; i < size(); i++) {
            newLst[newFirst + i] = lst[(nextFirst + 1 + i) % lst.length];
            lst[(nextFirst + i + 1) % lst.length] = null;
        }
        lst = newLst;
        nextLast = newFirst + size();
        nextFirst = newFirst - 1;
    }

    private void checkUsage() {
        if ((nextFirst - 1 + lst.length) % lst.length == nextLast) {
            resize(lst.length * 2);
        } else if (size() < (lst.length / 4) && lst.length > 8) {
            resize(lst.length / 2);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(T t) {
        checkUsage();
        lst[nextFirst] = t;
        size += 1;
        nextFirst = (nextFirst - 1 + lst.length) % lst.length;
    }

    @Override
    public void addLast(T t) {
        checkUsage();
        lst[nextLast] = t;
        size += 1;
        nextLast = (nextLast + 1) % lst.length;
    }

    @Override
    public T get(int index) {
        if (isEmpty()) {
            return null;
        }
        return lst[(nextFirst + 1 + index) % lst.length];
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T t = lst[(nextFirst + 1) % lst.length];
        lst[(nextFirst + 1) % lst.length] = null;
        nextFirst = (nextFirst + 1) % lst.length;
        size -= 1;
        return t;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T t = lst[(nextLast - 1 + lst.length) % lst.length];
        lst[(nextLast - 1 + lst.length) % lst.length] = null;
        nextLast = (nextLast - 1 + lst.length) % lst.length;
        size -= 1;
        return t;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size(); i++) {
            System.out.print(lst[(nextFirst + 1 + i) % 12] + " ");
        }
        System.out.println(" ");
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;

        public ArrayDequeIterator() {
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
        if (!(o instanceof Deque) || ((Deque<?>) o).size() != this.size()) {
            return false;
        }
        if (o == this) {
            return true;
        }
        for (int i = 0; i < this.size(); i++) {
            Object item = ((Deque<?>) o).get(i);
            if (!(this.get(i).equals(item))) {
                return false;
            }
        }
        return true;
    }

    private boolean contains(Object o) {
        for (T x : this) {
            if (o.equals(x)) {
                return true;
            }
        }
        return false;
    }
}