import java.util.*;

/** MODULE 3b - NP-Complete problems and approximation: Vertex Cover and Travelling Salesman. */
public class NPAlgos {

    /** True when every edge has at least one end inside the chosen set. */
    static boolean isCover(int[][] edges, Set<Integer> chosen) {
        for (int[] e : edges) {
            if (!chosen.contains(e[0]) && !chosen.contains(e[1])) return false;
        }
        return true;
    }

    /**
     * 2-approximation for Vertex Cover: pick any uncovered edge, take BOTH its ends, repeat.
     * The picked edges form a matching, and any real cover needs one end of each, so answer <= 2 x optimal.
     * Time O(E).
     */
    static Set<Integer> vertexCoverApprox(int[][] edges) {
        Set<Integer> cover = new TreeSet<>();
        for (int[] e : edges) {
            if (!cover.contains(e[0]) && !cover.contains(e[1])) {
                cover.add(e[0]);
                cover.add(e[1]);
            }
        }
        return cover;
    }

    /** Exact Vertex Cover by trying every subset of stations. Time O(2^n * E): fine only for small n. */
    static Set<Integer> vertexCoverExact(int n, int[][] edges) {
        Set<Integer> best = null;
        for (int mask = 0; mask < (1 << n); mask++) {
            if (best != null && Integer.bitCount(mask) >= best.size()) continue;
            Set<Integer> chosen = new TreeSet<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) chosen.add(i);
            }
            if (isCover(edges, chosen)) best = chosen;
        }
        return best;
    }

    /**
     * 2-approximation for metric TSP: build a Minimum Spanning Tree (Prim), then visit stations in DFS preorder.
     * Tour length <= 2 x MST weight <= 2 x optimal tour. Time O(n^2).
     * 'order' receives the tour (starts at 'start'; the trip back is not listed).
     */
    static int tspApprox(int[][] d, int start, List<Integer> order) {
        int n = d.length;
        int[] key = new int[n];
        int[] parent = new int[n];
        boolean[] inTree = new boolean[n];
        Arrays.fill(key, Integer.MAX_VALUE);
        key[start] = 0;
        parent[start] = -1;
        for (int round = 0; round < n; round++) {
            int u = -1;
            for (int v = 0; v < n; v++) {
                if (!inTree[v] && (u == -1 || key[v] < key[u])) u = v;
            }
            inTree[u] = true;
            for (int v = 0; v < n; v++) {
                if (!inTree[v] && d[u][v] < key[v]) {
                    key[v] = d[u][v];
                    parent[v] = u;
                }
            }
        }
        List<List<Integer>> children = new ArrayList<>();
        for (int i = 0; i < n; i++) children.add(new ArrayList<>());
        for (int v = 0; v < n; v++) {
            if (parent[v] != -1) children.get(parent[v]).add(v);
        }
        preorder(start, children, order);

        int cost = 0;
        for (int i = 0; i + 1 < order.size(); i++) cost += d[order.get(i)][order.get(i + 1)];
        cost += d[order.get(order.size() - 1)][start];
        return cost;
    }

    private static void preorder(int u, List<List<Integer>> children, List<Integer> order) {
        order.add(u);
        for (int v : children.get(u)) preorder(v, children, order);
    }
}
