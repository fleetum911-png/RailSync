import java.util.*;

/** MODULE 1 - String algorithms: KMP search, Edit Distance (DP), Longest Common Subsequence (DP). */
public class StringAlgos {

    /** Builds the KMP failure table: lps[i] = length of the longest proper prefix of p[0..i] that is also a suffix. */
    static int[] buildLps(String p) {
        int[] lps = new int[p.length()];
        int len = 0;
        for (int i = 1; i < p.length(); i++) {
            while (len > 0 && p.charAt(i) != p.charAt(len)) len = lps[len - 1];
            if (p.charAt(i) == p.charAt(len)) len++;
            lps[i] = len;
        }
        return lps;
    }

    /** KMP search. Returns every start index where pattern occurs in text. Time O(n + m). */
    static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> found = new ArrayList<>();
        if (pattern.isEmpty()) return found;
        int[] lps = buildLps(pattern);
        int j = 0;
        for (int i = 0; i < text.length(); i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j)) j = lps[j - 1];
            if (text.charAt(i) == pattern.charAt(j)) j++;
            if (j == pattern.length()) {
                found.add(i - j + 1);
                j = lps[j - 1];
            }
        }
        return found;
    }

    /** Edit distance (Levenshtein): fewest insert/delete/replace steps to turn a into b. Time O(n*m). */
    static int editDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    /** Index of the station whose name is closest to what the user typed. */
    static int closestStation(String typed) {
        int best = 0;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < Data.N; i++) {
            int d = editDistance(typed.trim(), Data.STATIONS[i]);
            if (d < bestDist) {
                bestDist = d;
                best = i;
            }
        }
        return best;
    }

    /** Longest Common Subsequence of two lists of words (station names). Time O(n*m). */
    static List<String> lcs(String[] a, String[] b) {
        int n = a.length, m = b.length;
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a[i - 1].equals(b[j - 1])) dp[i][j] = dp[i - 1][j - 1] + 1;
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        LinkedList<String> result = new LinkedList<>();
        int i = n, j = m;
        while (i > 0 && j > 0) {
            if (a[i - 1].equals(b[j - 1])) {
                result.addFirst(a[i - 1]);
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return result;
    }
}
