import java.util.*;

/**
 * Sample railway network shared by every module.
 * Distances and capacities are approximate and used for demonstration only.
 */
public class Data {

    static final String[] STATIONS = {
        "Hyderabad", "Warangal", "Vijayawada", "Visakhapatnam",
        "Nagpur", "Chennai", "Tirupati", "Bengaluru"
    };
    static final int N = STATIONS.length;

    // Each track: {stationA, stationB, distance in km, capacity in trains/day}
    static final int[][] TRACKS = {
        {0, 1, 150, 10}, {0, 2, 270, 8}, {0, 4, 500, 5}, {0, 7, 570, 6},
        {1, 2, 220, 6},  {1, 4, 350, 4},
        {2, 3, 350, 7},  {2, 5, 430, 8}, {2, 6, 400, 4},
        {3, 4, 870, 3},
        {5, 6, 140, 6},  {5, 7, 350, 7}, {6, 7, 250, 4}
    };

    // Crew members and the station each one lives at
    static final String[] CREW_NAMES = {"Ravi", "Anita", "Suresh", "Meena", "Kiran", "Latha"};
    static final int[] CREW_HOME = {0, 0, 2, 5, 7, 1};

    // Cargo that can be loaded in a wagon: weight in tonnes, value in thousand rupees
    static final String[] CARGO_NAMES = {"Rice bags", "Medicines", "Electronics", "Textiles", "Cement", "Fruits"};
    static final int[] CARGO_WEIGHT = {40, 10, 25, 30, 50, 20};
    static final int[] CARGO_VALUE = {60, 100, 150, 90, 40, 50};

    /** Returns the station index for a name (ignores case), or -1 if not found. */
    static int findStation(String name) {
        for (int i = 0; i < N; i++) {
            if (STATIONS[i].equalsIgnoreCase(name.trim())) return i;
        }
        return -1;
    }

    /** Tracks as plain edges {a, b} (used by vertex cover and min-cut). */
    static int[][] edgePairs() {
        int[][] e = new int[TRACKS.length][2];
        for (int i = 0; i < TRACKS.length; i++) {
            e[i][0] = TRACKS[i][0];
            e[i][1] = TRACKS[i][1];
        }
        return e;
    }
}
