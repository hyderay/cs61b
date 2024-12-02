package deque;

public class ArrayDeque<Item> {
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
        System.arraycopy(lst, 0, newLst, newFirst + 1, size());
        lst = newLst;
        nextLast = newFirst + 1 + size();
        nextFirst = newFirst;
    }

    private void checkUsage() {
        if (nextFirst == -1) {
            resize(lst.length * 2);
        } else if (nextLast == lst.length) {
            resize(lst.length * 2);
        } else if (size() < (lst.length) / 4 && size() > 8) {
            resize(lst.length / 2);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void addFirst(Item t) {
        checkUsage();
        lst[nextFirst] = t;
        size += 1;
        nextFirst -= 1;
    }

    public void addLast(Item t) {
        checkUsage();
        lst[nextLast] = t;
        size += 1;
        nextLast += 1;
    }

    public Item get(int index) {
        if (isEmpty()) {
            return null;
        }
        return lst[index + nextFirst + 1];
    }

    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Item t = lst[nextFirst + 1];
        lst[nextFirst + 1] = null;
        nextFirst += 1;
        size -= 1;
        return t;
    }

    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }
        Item t = lst[nextLast - 1];
        lst[nextLast - 1] = null;
        nextLast -= 1;
        size -= 1;
        return t;
    }

    public void printDeque() {
        for (int i = 0; i < size(); i++) {
            System.out.print(lst[i] + " ");
        }
        System.out.println(" ");
    }
}