package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.introcs.StdRandom;

public class TestArrayDequeEC {
    @Test
    public void randomTest() {
        ArrayDequeSolution<Integer> solution = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> student = new StudentArrayDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i++) {
            int random = StdRandom.uniform(4);
            if (random == 0) {
                int ranNum = StdRandom.uniform(100);
                solution.addFirst(ranNum);
                student.addFirst(ranNum);
                int expected = solution.get(0);
                int actual = student.get(0);
                assertEquals("The first integer should be " + expected + ". But get "
                                + actual, expected, actual);

            } else if (random == 1) {
                int ranNum = StdRandom.uniform(100);
                solution.addLast(ranNum);
                student.addLast(ranNum);
                int expected = solution.get(solution.size() - 1);
                int actual = student.get(student.size() - 1);
                assertEquals("The last integer should be " + expected +
                        ". But get " + actual, expected, actual);

            } else if (random == 2 && !solution.isEmpty() && !student.isEmpty()) {
                Integer expected = solution.removeFirst();
                Integer actual = student.removeFirst();
                assertEquals(expected + " should be remove as the first one. But " +
                        actual + " is removed", expected, actual);

            } else if (random == 3 && !solution.isEmpty() && !student.isEmpty()) {
                Integer expected = solution.removeLast();
                Integer actual = student.removeLast();
                assertEquals(expected + " should be remove as the last one. But " +
                        actual + " is removed", expected, actual);
            }
        }
    }
}