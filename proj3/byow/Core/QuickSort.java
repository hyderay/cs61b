package byow.Core;

import java.util.*;

public class QuickSort {
    public static void quickSort(List<Edge> edges, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(edges, low, high);
            quickSort(edges, low, pivotIndex);
            quickSort(edges, pivotIndex + 1, high);
        }
    }

    private static int partition(List<Edge> edges, int low, int high) {
        double pivot = edges.get(low).getWeight();
        int left = low - 1;
        int right = high + 1;

        while (true) {
            left++;
            while (edges.get(left).getWeight() < pivot) {
                left++;
            }

            right--;
            while (edges.get(right).getWeight() > pivot) {
                right--;
            }

            if (left >= right) {
                return right;
            }
            Collections.swap(edges, left, right);
        }
    }
}
