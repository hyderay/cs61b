package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        BuggyAList<Integer> bugList = new BuggyAList<>();
        AListNoResizing<Integer> corrList = new AListNoResizing<>();

        bugList.addLast(4);
        bugList.addLast(5);
        bugList.addLast(6);

        corrList.addLast(4);
        corrList.addLast(5);
        corrList.addLast(6);

        assertEquals(corrList.removeLast(), bugList.removeLast());
        assertEquals(corrList.removeLast(), bugList.removeLast());
        assertEquals(corrList.removeLast(), bugList.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> K = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                K.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                assertEquals(L.size(), K.size());
            } else if (operationNumber == 2) {
                if (L.size() == 0) {
                    continue;
                }
                assertEquals(L.getLast(), L.getLast());
            } else if (operationNumber == 3) {
                if (L.size() == 0) {
                    continue;
                }
                assertEquals(L.removeLast(), K.removeLast());
            }
        }

    }
}
