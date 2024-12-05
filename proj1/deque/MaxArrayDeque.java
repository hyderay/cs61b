package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> t;

    public MaxArrayDeque(Comparator<T> t) {
        this.t = t;
    }


}