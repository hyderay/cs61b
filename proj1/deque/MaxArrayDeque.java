package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> t;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.t = c;
    }

    public T max() {
        return max(t);
    }

    public T max(Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }
        T maxE = this.get(0);
        for (int i = 1; i < this.size(); i++) {
            T pointer = this.get(i);
            if (c.compare(pointer, maxE) > 0) {
                maxE = pointer;
            }
        }
        return maxE;
    }
}