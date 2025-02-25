package byow.Core;

import java.util.*;

public class QuickSort {
    public static void quickSort(List<Edge> edges, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(edges, low, high);
            quickSort(edges, low, pivotIndex - 1);
            quickSort(edges, pivotIndex + 1, high);
        }
    }

    private static int partition(List<Edge> edges, int low, int high) {
        double pivot = edges.get(low).getWeight();
        int left = low;
        int right = high;

        while (true) {
            while (left <= high && edges.get(left).getWeight() < pivot) {
                left++;
            }
            while (right >= low && edges.get(right).getWeight() > pivot) {
                right--;
            }
            if (left >= right) {
                return right;
            }

            Collections.swap(edges, left, right);
        }
    }
}
