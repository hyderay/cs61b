package deque;

import java.util.Iterator;

public class ArrayDeque<Item> implements Deque<Item>, Iterable<Item> {
    private Item[] lst;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        lst = (Item[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    private void resize(int capacity) {
        Item[] newLst = (Item[]) new Object[capacity];
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
        } else if (size() < (lst.length) / 4 && size() > 8) {
            resize(lst.length / 2);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(Item t) {
        checkUsage();
        lst[nextFirst] = t;
        size += 1;
        nextFirst = (nextFirst - 1 + lst.length) % lst.length;
    }

    @Override
    public void addLast(Item t) {
        checkUsage();
        lst[nextLast] = t;
        size += 1;
        nextLast = (nextLast + 1) % lst.length;
    }

    @Override
    public Item get(int index) {
        if (isEmpty()) {
            return null;
        }
        return lst[(nextFirst + 1 + index) % lst.length];
    }

    @Override
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Item t = lst[(nextFirst + 1) % lst.length];
        lst[(nextFirst + 1) % lst.length] = null;
        nextFirst = (nextFirst + 1) % lst.length;
        size -= 1;
        return t;
    }

    @Override
    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }
        Item t = lst[(nextLast - 1 + lst.length) % lst.length];
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

    public Iterator<Item> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<Item> {
        private int wizPos;

        public ArrayDequeIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size();
        }

        public Item next() {
            Item returnItem = get(wizPos);
            wizPos++;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ArrayDeque sets) {
            if (size() != sets.size()) {
                return false;
            }
            for (Item x : this) {
                if (!sets.contains(x)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean contains(Object o) {
        for (Item x : this) {
            if (o.equals(x)) {
                return true;
            }
        }
        return false;
    }
}