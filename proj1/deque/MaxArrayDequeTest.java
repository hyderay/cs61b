package deque;

import java.util.Comparator;
import org.junit.Test;
import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    private static class IntComparator implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    public static Comparator<Integer> getIntComparator() {
        return new IntComparator();
    }

    @Test
    public void testInteger() {
        MaxArrayDeque<Integer> array1 = new MaxArrayDeque<>(getIntComparator());
        array1.addLast(1);
        array1.addLast(2);
        array1.addLast(0);
        array1.addLast(5);
        int actual = array1.max();

        assertEquals(5, actual);
    }

    private static class StringComparator implements Comparator<String> {
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    public static Comparator<String> getStringComparator() {
        return new StringComparator();
    }

    @Test
    public void testString() {
        MaxArrayDeque<String> array2 = new MaxArrayDeque<>(getStringComparator());
        array2.addLast("panda");
        array2.addLast("cat");
        array2.addLast("dog");
        array2.addLast("hello");
        String actual = array2.max();

        assertEquals("panda", actual);
    }
}
