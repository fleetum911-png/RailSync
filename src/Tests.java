import java.util.*;

/** MODULE 5 - Built-in test cases. Menu option 15 runs all of them and prints PASS / FAIL. */
public class Tests {

    static int passed = 0, failed = 0;

    static void check(String name, boolean ok) {
        System.out.println("  [" + (ok ? "PASS" : "FAIL") + "] " + name);
        if (ok) passed++;
        else failed++;
    }

    static boolean visitsAll(List<Integer> tour, int n) {
        return tour.size() == n && new HashSet<>(tour).size() == n;
    }

    /** Brute force minimum cut: try every way of splitting the stations into two groups. */
    static int bruteForceMinCut(int n, int[][] edges) {
        int best = Integer.MAX_VALUE;
        for (int mask = 1; mask < (1 << n) - 1; mask++) {
            int cut = 0;
            for (int[] e : edges) {
                boolean a = (mask & (1 << e[0])) != 0, b = (mask & (1 << e[1])) != 0;
                if (a != b) cut++;
            }
            best = Math.min(best, cut);
        }
        return best;
    }

    /** Brute force minimum s-t cut (max-flow min-cut theorem): try every group of stations that contains s but not t. */
    static int bruteForceStCut(int[][] cap, int s, int t) {
        int n = cap.length, best = Integer.MAX_VALUE;
        for (int mask = 0; mask < (1 << n); mask++) {
            if ((mask & (1 << s)) == 0 || (mask & (1 << t)) != 0) continue;
            int cut = 0;
            for (int u = 0; u < n; u++) {
                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << u)) != 0 && (mask & (1 << v)) == 0) cut += cap[u][v];
                }
            }
            best = Math.min(best, cut);
        }
        return best;
    }

    static int sumOf(int[] v) {
        int s = 0;
        for (int x : v) s += x;
        return s;
    }

    static void runAll() {
        passed = 0;
        failed = 0;
        Random rnd = new Random(42);
        int[][] edges = Data.edgePairs();

        System.out.println("\n-- String algorithms --");
        check("KMP finds 'ana' in 'bananas' at positions 1 and 3",
              StringAlgos.kmpSearch("bananas", "ana").equals(Arrays.asList(1, 3)));
        check("KMP returns nothing when the pattern is absent",
              StringAlgos.kmpSearch("hyderabad", "xyz").isEmpty());
        check("KMP failure table of 'ababaca' is [0,0,1,2,3,0,1]",
              Arrays.equals(StringAlgos.buildLps("ababaca"), new int[]{0, 0, 1, 2, 3, 0, 1}));
        check("Edit distance kitten -> sitting is 3",
              StringAlgos.editDistance("kitten", "sitting") == 3);
        check("Spell-checker suggests Hyderabad for 'Hydrabad'",
              Data.STATIONS[StringAlgos.closestStation("Hydrabad")].equals("Hyderabad"));
        check("LCS of [A,B,C,D,E] and [A,C,E,F] is [A,C,E]",
              StringAlgos.lcs(new String[]{"A", "B", "C", "D", "E"}, new String[]{"A", "C", "E", "F"})
                         .equals(Arrays.asList("A", "C", "E")));

        System.out.println("\n-- Dynamic programming --");
        check("Shortest Hyderabad -> Chennai is 700 km",
              DPAlgos.DIST[0][5] == 700);
        check("Shortest path Hyderabad -> Visakhapatnam goes through Vijayawada",
              DPAlgos.path(0, 3).equals(Arrays.asList(0, 2, 3)));
        check("Distances are symmetric",
              DPAlgos.DIST[2][7] == DPAlgos.DIST[7][2]);
        List<Integer> picked = new ArrayList<>();
        check("Knapsack: weights {10,20,30}, values {60,100,120}, capacity 50 gives 220",
              DPAlgos.knapsack(new int[]{10, 20, 30}, new int[]{60, 100, 120}, 50, picked) == 220);
        check("Knapsack picks items 2 and 3 (indexes 1 and 2)",
              new HashSet<>(picked).equals(new HashSet<>(Arrays.asList(1, 2))));
        check("Knapsack with capacity 0 gives 0",
              DPAlgos.knapsack(new int[]{5}, new int[]{9}, 0, new ArrayList<>()) == 0);
        Train t = new Train(1, "Test", new int[]{0, 2, 5}, 100, 500);
        check("Train Hyderabad > Vijayawada > Chennai has distance 700 km", t.distance == 700);

        System.out.println("\n-- Network flow --");
        int[][] clrs = new int[6][6]; // classic textbook example, answer 23
        clrs[0][1] = 16; clrs[0][2] = 13; clrs[1][3] = 12; clrs[2][1] = 4; clrs[2][4] = 14;
        clrs[3][2] = 9;  clrs[3][5] = 20; clrs[4][3] = 7;  clrs[4][5] = 4;
        check("Max flow on the textbook network is 23",
              FlowAlgos.maxFlow(clrs, 0, 5, null) == 23);
        int[][] net = FlowAlgos.trackCapacities();
        check("Max flow Hyderabad -> Chennai equals the brute-force minimum cut (" + bruteForceStCut(net, 0, 5) + ")",
              FlowAlgos.maxFlow(FlowAlgos.trackCapacities(), 0, 5, null) == bruteForceStCut(net, 0, 5));
        check("Max flow Visakhapatnam -> Hyderabad equals the brute-force minimum cut (" + bruteForceStCut(net, 3, 0) + ")",
              FlowAlgos.maxFlow(FlowAlgos.trackCapacities(), 3, 0, null) == bruteForceStCut(net, 3, 0));
        boolean[][] full = {{true, true, false}, {false, true, false}, {false, true, true}};
        int[] m1 = FlowAlgos.bipartiteMatching(full);
        check("Matching: 3 workers and 3 jobs can all be matched",
              Arrays.stream(m1).filter(x -> x >= 0).count() == 3);
        boolean[][] clash = {{true, false}, {true, false}, {true, false}};
        check("Matching: 3 workers who all want the same job -> only 1 matched",
              Arrays.stream(FlowAlgos.bipartiteMatching(clash)).filter(x -> x >= 0).count() == 1);

        System.out.println("\n-- NP-complete problems and approximation --");
        List<Integer> exactTour = new ArrayList<>(), approxTour = new ArrayList<>();
        int exact = DPAlgos.tspExact(DPAlgos.DIST, 0, exactTour);
        int approx = NPAlgos.tspApprox(DPAlgos.DIST, 0, approxTour);
        check("Exact TSP tour visits every station once", visitsAll(exactTour, Data.N));
        check("Approximate TSP tour visits every station once", visitsAll(approxTour, Data.N));
        check("Exact tour is never longer than the approximate tour (" + exact + " <= " + approx + ")", exact <= approx);
        check("Approximate tour is within 2 x optimal (" + approx + " <= " + 2 * exact + ")", approx <= 2 * exact);
        Set<Integer> vcApprox = NPAlgos.vertexCoverApprox(edges);
        Set<Integer> vcExact = NPAlgos.vertexCoverExact(Data.N, edges);
        check("Approximate vertex cover covers every track", NPAlgos.isCover(edges, vcApprox));
        check("Exact vertex cover covers every track", NPAlgos.isCover(edges, vcExact));
        check("Approximate cover is within 2 x optimal (" + vcApprox.size() + " <= " + 2 * vcExact.size() + ")",
              vcApprox.size() <= 2 * vcExact.size());

        System.out.println("\n-- Randomized and parallel algorithms --");
        RandomParallel.CutResult cut = RandomParallel.kargerMinCut(Data.N, edges, 500, rnd);
        int truth = bruteForceMinCut(Data.N, edges);
        check("Karger min-cut equals the brute-force answer (" + cut.size + " == " + truth + ")", cut.size == truth);
        Integer[] data = new Integer[1000];
        for (int i = 0; i < data.length; i++) data[i] = rnd.nextInt(10000);
        Integer[] expected = data.clone();
        Arrays.sort(expected);
        RandomParallel.randomizedQuickSort(data, Comparator.naturalOrder(), rnd);
        check("Randomized QuickSort sorts 1000 random numbers", Arrays.equals(data, expected));
        Integer[] sortedInput = new Integer[500];
        for (int i = 0; i < sortedInput.length; i++) sortedInput[i] = i;
        Integer[] copy = sortedInput.clone();
        RandomParallel.randomizedQuickSort(copy, Comparator.naturalOrder(), rnd);
        check("Randomized QuickSort handles already-sorted input", Arrays.equals(copy, sortedInput));
        int[] big = new int[300_000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(1000);
        check("Parallel revenue total equals sequential total", RandomParallel.parallelRevenue(big) == RandomParallel.sequentialRevenue(big));
        int[] b1 = big.clone(), b2 = big.clone();
        RandomParallel.parallelSort(b1);
        Arrays.sort(b2);
        check("Parallel merge sort gives the same result as Arrays.sort", Arrays.equals(b1, b2));
        int[] b3 = big.clone();
        RandomParallel.sequentialSort(b3);
        check("Sequential merge sort gives the same result as Arrays.sort", Arrays.equals(b3, b2));

        System.out.println("\nResult: " + passed + " passed, " + failed + " failed.");
    }
}
