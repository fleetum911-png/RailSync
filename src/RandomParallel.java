import java.util.*;
import java.util.concurrent.*;

/** MODULE 4 - Randomized algorithms (QuickSort, Karger min-cut) and Parallel algorithms (Fork/Join sum and sort). */
public class RandomParallel {

    // ---------------- Randomized QuickSort ----------------

    /** QuickSort with a random pivot. Expected time O(n log n) for every input. */
    static <T> void randomizedQuickSort(T[] a, Comparator<T> cmp, Random rnd) {
        rqs(a, 0, a.length - 1, cmp, rnd);
    }

    private static <T> void rqs(T[] a, int lo, int hi, Comparator<T> cmp, Random rnd) {
        if (lo >= hi) return;
        int p = lo + rnd.nextInt(hi - lo + 1);
        swap(a, p, hi);
        int i = partition(a, lo, hi, cmp);
        rqs(a, lo, i - 1, cmp, rnd);
        rqs(a, i + 1, hi, cmp, rnd);
    }

    /** Plain QuickSort that always uses the first element as pivot. Worst case O(n^2) on sorted input. */
    static <T> void firstPivotQuickSort(T[] a, Comparator<T> cmp) {
        fpqs(a, 0, a.length - 1, cmp);
    }

    private static <T> void fpqs(T[] a, int lo, int hi, Comparator<T> cmp) {
        if (lo >= hi) return;
        swap(a, lo, hi);
        int i = partition(a, lo, hi, cmp);
        fpqs(a, lo, i - 1, cmp);
        fpqs(a, i + 1, hi, cmp);
    }

    private static <T> int partition(T[] a, int lo, int hi, Comparator<T> cmp) {
        T pivot = a[hi];
        int i = lo;
        for (int j = lo; j < hi; j++) {
            if (cmp.compare(a[j], pivot) < 0) {
                swap(a, i, j);
                i++;
            }
        }
        swap(a, i, hi);
        return i;
    }

    private static <T> void swap(T[] a, int i, int j) {
        T tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    // ---------------- Karger's randomized min-cut ----------------

    static class CutResult {
        int size;             // number of tracks that must be cut
        boolean[] sideA;      // which stations are on the first side
        List<int[]> crossing = new ArrayList<>(); // the tracks that are cut
    }

    private static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    /**
     * Karger's algorithm: shuffle the edges, merge stations edge by edge until only 2 groups remain,
     * then count the edges between the groups. Repeat 'trials' times and keep the smallest cut.
     * One trial finds a minimum cut with probability >= 2 / (n(n-1)); more trials make failure very unlikely.
     */
    static CutResult kargerMinCut(int n, int[][] edges, int trials, Random rnd) {
        CutResult best = null;
        for (int t = 0; t < trials; t++) {
            int[] parent = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
            List<int[]> shuffled = new ArrayList<>(Arrays.asList(edges));
            Collections.shuffle(shuffled, rnd);

            int groups = n;
            for (int[] e : shuffled) {
                if (groups == 2) break;
                int ra = find(parent, e[0]), rb = find(parent, e[1]);
                if (ra != rb) {
                    parent[ra] = rb;
                    groups--;
                }
            }
            CutResult r = new CutResult();
            r.sideA = new boolean[n];
            int rootOfZero = find(parent, 0);
            for (int i = 0; i < n; i++) r.sideA[i] = (find(parent, i) == rootOfZero);
            for (int[] e : edges) {
                if (r.sideA[e[0]] != r.sideA[e[1]]) {
                    r.size++;
                    r.crossing.add(e);
                }
            }
            if (best == null || r.size < best.size) best = r;
        }
        return best;
    }

    // ---------------- Parallel algorithms (Fork/Join) ----------------

    static final ForkJoinPool POOL = new ForkJoinPool(); // uses all CPU cores
    private static final int THRESHOLD = 50_000;         // below this size, work sequentially

    /** Net revenue of one ticket in paise: fare + 5% GST - a distance-style discount formula. */
    static long netPaise(int fare) {
        return (long) ((fare * 1.05 - Math.sqrt(fare) * Math.log(fare)) * 100);
    }

    static long sequentialRevenue(int[] a) {
        long s = 0;
        for (int x : a) s += netPaise(x);
        return s;
    }

    /** Divide the tickets in halves, add up each half in parallel, then add the two results. */
    static class RevenueTask extends RecursiveTask<Long> {
        final int[] a;
        final int lo, hi;

        RevenueTask(int[] a, int lo, int hi) {
            this.a = a;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected Long compute() {
            if (hi - lo <= THRESHOLD) {
                long s = 0;
                for (int i = lo; i < hi; i++) s += netPaise(a[i]);
                return s;
            }
            int mid = (lo + hi) >>> 1;
            RevenueTask left = new RevenueTask(a, lo, mid);
            RevenueTask right = new RevenueTask(a, mid, hi);
            left.fork();
            long r = right.compute();
            return left.join() + r;
        }
    }

    static long parallelRevenue(int[] a) {
        return POOL.invoke(new RevenueTask(a, 0, a.length));
    }

    static void sequentialSort(int[] a) {
        mergeSort(a, new int[a.length], 0, a.length);
    }

    static void parallelSort(int[] a) {
        POOL.invoke(new SortTask(a, new int[a.length], 0, a.length));
    }

    /** Sort the two halves in parallel, then merge them. */
    static class SortTask extends RecursiveAction {
        final int[] a, tmp;
        final int lo, hi;

        SortTask(int[] a, int[] tmp, int lo, int hi) {
            this.a = a;
            this.tmp = tmp;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected void compute() {
            if (hi - lo <= THRESHOLD) {
                mergeSort(a, tmp, lo, hi);
                return;
            }
            int mid = (lo + hi) >>> 1;
            invokeAll(new SortTask(a, tmp, lo, mid), new SortTask(a, tmp, mid, hi));
            merge(a, tmp, lo, mid, hi);
        }
    }

    // Ordinary merge sort on the half-open range [lo, hi)
    private static void mergeSort(int[] a, int[] tmp, int lo, int hi) {
        if (hi - lo < 2) return;
        int mid = (lo + hi) >>> 1;
        mergeSort(a, tmp, lo, mid);
        mergeSort(a, tmp, mid, hi);
        merge(a, tmp, lo, mid, hi);
    }

    private static void merge(int[] a, int[] tmp, int lo, int mid, int hi) {
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) tmp[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
        while (i < mid) tmp[k++] = a[i++];
        while (j < hi) tmp[k++] = a[j++];
        System.arraycopy(tmp, lo, a, lo, hi - lo);
    }
}
