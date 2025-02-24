package byow.Core;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TestQuickSort {
    @Test
    public void testDouble() {
        ArrayList<Double> actualEdges = new ArrayList<>();
        ArrayList<Double> expectedEdges = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            double random = 100 * Math.random();
            actualEdges.add(random);
            expectedEdges.add(random);
        }

        QuickSort.quickSort(actualEdges, 0, 9);
        Collections.sort(expectedEdges);

        assertEquals("Sorting failed for large random input!", expectedEdges, actualEdges);
    }
}
