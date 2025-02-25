package byow.Core.Test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

import byow.Core.Edge;
import byow.Core.QuickSort;

/**
 * Remember to change the quick sort's type to double when running the test.
 * */
public class TestQuickSort {
    @Test
    public void testDouble() {
        Random random = new Random();
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            edges.add(new Edge(random.nextDouble() * 100,
                    0, 0));
        }

        ArrayList<Double> expected = new ArrayList<>();
        for (Edge edge : edges) {
            expected.add(edge.getWeight());
        }
        Collections.sort(expected);

        QuickSort.quickSort(edges, 0, edges.size() - 1);

        for (int i = 0; i < edges.size(); i++) {
            assertEquals("Should be equal", expected.get(i), edges.get(i).getWeight(), 0.0001);
        }
    }
}
