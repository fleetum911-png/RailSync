import java.util.*;

/** MODULE 3a - Network Flow: Edmonds-Karp maximum flow and bipartite matching built on top of it. */
public class FlowAlgos {

    /**
     * Edmonds-Karp (Ford-Fulkerson with BFS). Time O(V * E^2).
     * 'cap' is changed into the residual graph. 'log' receives one line per augmenting path.
     */
    static int maxFlow(int[][] cap, int s, int t, List<String> log) {
        return maxFlow(cap, s, t, log, null);
    }

    /** Same as above; when 'labels' is given, paths in the log use those names instead of node numbers. */
    static int maxFlow(int[][] cap, int s, int t, List<String> log, String[] labels) {
        int n = cap.length;
        int total = 0;
        while (true) {
            int[] parent = new int[n];
            Arrays.fill(parent, -1);
            parent[s] = s;
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(s);
            while (!queue.isEmpty() && parent[t] == -1) {
                int u = queue.poll();
                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && cap[u][v] > 0) {
                        parent[v] = u;
                        queue.add(v);
                    }
                }
            }
            if (parent[t] == -1) break; // no more augmenting paths

            int push = Integer.MAX_VALUE;
            for (int v = t; v != s; v = parent[v]) push = Math.min(push, cap[parent[v]][v]);

            LinkedList<Integer> path = new LinkedList<>();
            for (int v = t; v != s; v = parent[v]) {
                cap[parent[v]][v] -= push;
                cap[v][parent[v]] += push;
                path.addFirst(v);
            }
            path.addFirst(s);
            if (log != null) {
                StringBuilder sb = new StringBuilder();
                for (int v : path) {
                    if (sb.length() > 0) sb.append(" > ");
                    sb.append(labels == null ? String.valueOf(v) : labels[v]);
                }
                log.add(sb + "  carries " + push + " train(s)/day");
            }
            total += push;
        }
        return total;
    }

    /** Capacity matrix of the track network (every track works in both directions). */
    static int[][] trackCapacities() {
        int[][] cap = new int[Data.N][Data.N];
        for (int[] t : Data.TRACKS) {
            cap[t[0]][t[1]] = t[3];
            cap[t[1]][t[0]] = t[3];
        }
        return cap;
    }

    /**
     * Bipartite matching using max flow: source -> left (cap 1) -> right (cap 1) -> sink.
     * Returns match[left] = right, or -1 when that left item cannot be matched.
     */
    static int[] bipartiteMatching(boolean[][] canDo) {
        int left = canDo.length;
        int right = left == 0 ? 0 : canDo[0].length;
        int s = left + right, t = left + right + 1;
        int[][] cap = new int[left + right + 2][left + right + 2];
        for (int l = 0; l < left; l++) cap[s][l] = 1;
        for (int r = 0; r < right; r++) cap[left + r][t] = 1;
        for (int l = 0; l < left; l++) {
            for (int r = 0; r < right; r++) {
                if (canDo[l][r]) cap[l][left + r] = 1;
            }
        }
        maxFlow(cap, s, t, null);
        int[] match = new int[left];
        Arrays.fill(match, -1);
        for (int l = 0; l < left; l++) {
            for (int r = 0; r < right; r++) {
                if (canDo[l][r] && cap[l][left + r] == 0) match[l] = r; // this edge carries flow
            }
        }
        return match;
    }
}
