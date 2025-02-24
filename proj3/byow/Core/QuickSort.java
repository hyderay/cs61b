package byow.Core;

import java.util.*;

public class QuickSort {
    public static void quickSort(List<Double> edges, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(edges, low, high);
            quickSort(edges, low, pivotIndex - 1);
            quickSort(edges, pivotIndex + 1, high);
        }
    }

    private static int partition(List<Double> edges, int low, int high) {
        double pivot = edges.get(low);
        int left = low;
        int right = high;

        while (true) {
            while (left <= high && edges.get(left) < pivot) {
                left++;
            }
            while (right >= low && edges.get(right) > pivot) {
                right--;
            }
            if (left >= right) {
                return right;
            }

            Collections.swap(edges, left, right);
        }
    }
}
