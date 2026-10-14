import java.util.*;

/** MODULE 2 - Advanced Dynamic Programming: Floyd-Warshall, 0/1 Knapsack, TSP (Held-Karp). */
public class DPAlgos {

    static final int INF = 1_000_000;
    static final int[][] DIST = new int[Data.N][Data.N]; // shortest km between every pair of stations
    static final int[][] NEXT = new int[Data.N][Data.N]; // next station on the shortest path

    static {
        floydWarshall();
    }

    /** All-pairs shortest paths. DP idea: allow station k as a middle stop, one k at a time. Time O(V^3). */
    static void floydWarshall() {
        for (int i = 0; i < Data.N; i++) {
            for (int j = 0; j < Data.N; j++) {
                DIST[i][j] = (i == j) ? 0 : INF;
                NEXT[i][j] = -1;
            }
        }
        for (int[] t : Data.TRACKS) {
            DIST[t[0]][t[1]] = DIST[t[1]][t[0]] = t[2];
            NEXT[t[0]][t[1]] = t[1];
            NEXT[t[1]][t[0]] = t[0];
        }
        for (int k = 0; k < Data.N; k++) {
            for (int i = 0; i < Data.N; i++) {
                for (int j = 0; j < Data.N; j++) {
                    if (DIST[i][k] + DIST[k][j] < DIST[i][j]) {
                        DIST[i][j] = DIST[i][k] + DIST[k][j];
                        NEXT[i][j] = NEXT[i][k];
                    }
                }
            }
        }
    }

    /** Station indexes on the shortest path from 'from' to 'to'. */
    static List<Integer> path(int from, int to) {
        List<Integer> p = new ArrayList<>();
        int cur = from;
        p.add(cur);
        while (cur != to) {
            cur = NEXT[cur][to];
            p.add(cur);
        }
        return p;
    }

    /** 0/1 Knapsack: best total value that fits in 'cap'. 'chosen' receives the picked item indexes. Time O(n*cap). */
    static int knapsack(int[] weight, int[] value, int cap, List<Integer> chosen) {
        int n = weight.length;
        int[][] dp = new int[n + 1][cap + 1];
        for (int i = 1; i <= n; i++) {
            for (int c = 0; c <= cap; c++) {
                dp[i][c] = dp[i - 1][c];
                if (weight[i - 1] <= c) {
                    dp[i][c] = Math.max(dp[i][c], dp[i - 1][c - weight[i - 1]] + value[i - 1]);
                }
            }
        }
        int c = cap;
        for (int i = n; i >= 1; i--) {
            if (dp[i][c] != dp[i - 1][c]) {
                chosen.add(i - 1);
                c -= weight[i - 1];
            }
        }
        return dp[n][cap];
    }

    /**
     * Exact Travelling Salesman tour using bitmask DP (Held-Karp). Time O(2^n * n^2).
     * dp[mask][last] = cheapest way to visit the stations in 'mask', ending at 'last'.
     * 'order' receives the tour (starts at 'start'; the trip back to start is not listed).
     */
    static int tspExact(int[][] d, int start, List<Integer> order) {
        int n = d.length;
        int full = 1 << n;
        int[][] dp = new int[full][n];
        int[][] parent = new int[full][n];
        for (int[] row : dp) Arrays.fill(row, INF);
        dp[1 << start][start] = 0;
        parent[1 << start][start] = -1;

        for (int mask = 0; mask < full; mask++) {
            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) == 0 || dp[mask][last] >= INF) continue;
                for (int next = 0; next < n; next++) {
                    if ((mask & (1 << next)) != 0) continue;
                    int newMask = mask | (1 << next);
                    int cost = dp[mask][last] + d[last][next];
                    if (cost < dp[newMask][next]) {
                        dp[newMask][next] = cost;
                        parent[newMask][next] = last;
                    }
                }
            }
        }

        int best = INF, bestLast = -1;
        for (int last = 0; last < n; last++) {
            if (last == start) continue;
            int total = dp[full - 1][last] + d[last][start];
            if (total < best) {
                best = total;
                bestLast = last;
            }
        }
        int mask = full - 1, cur = bestLast;
        while (cur != -1) {
            order.add(0, cur);
            int p = parent[mask][cur];
            mask ^= (1 << cur);
            cur = p;
        }
        return best;
    }
}
