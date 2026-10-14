import java.util.*;

/**
 * RailSync - Intelligent Railway Operations Platform
 * Main program: a simple numbered menu. Each option uses one DSA-3 algorithm on railway data.
 * Compile:  javac *.java      Run:  java Railsync
 */
public class Railsync {

    static final Scanner in = new Scanner(System.in);
    static final List<Train> trains = new ArrayList<>();
    static final Random rnd = new Random();

    public static void main(String[] args) {
        loadSampleTrains();
        try {
            while (true) {
                printMenu();
                int choice = readInt("Enter your choice: ");
                System.out.println();
                switch (choice) {
                    case 1: addTrain(); break;
                    case 2: showTrains(); break;
                    case 3: searchTrains(); break;
                    case 4: spellCheckStation(); break;
                    case 5: commonStops(); break;
                    case 6: shortestDistance(); break;
                    case 7: loadCargo(); break;
                    case 8: maxTrains(); break;
                    case 9: assignCrew(); break;
                    case 10: inspectionTour(); break;
                    case 11: placeCheckers(); break;
                    case 12: weakestLink(); break;
                    case 13: sortTrains(); break;
                    case 14: speedTest(); break;
                    case 15: Tests.runAll(); break;
                    case 0:
                        System.out.println("Thank you for using RailSync. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 0 to 15.");
                }
                System.out.print("\nPress Enter to return to the menu...");
                in.nextLine();
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nInput ended. Goodbye!");
        }
    }

    // ------------------------------------------------------------------ menu and input helpers

    static void printMenu() {
        System.out.println("\n==================================================");
        System.out.println("   RailSync - Intelligent Railway Operations");
        System.out.println("==================================================");
        System.out.println(" TRAIN MANAGEMENT");
        System.out.println("   1. Add a new train");
        System.out.println("   2. Show all trains");
        System.out.println(" SEARCH AND TEXT                (String algorithms)");
        System.out.println("   3. Search trains by keyword          [KMP]");
        System.out.println("   4. Station name spell-checker        [Edit Distance]");
        System.out.println("   5. Common stops of two trains        [LCS]");
        System.out.println(" ROUTES AND LOADING             (Dynamic Programming)");
        System.out.println("   6. Shortest distance between stations [Floyd-Warshall]");
        System.out.println("   7. Load cargo into a wagon           [0/1 Knapsack]");
        System.out.println(" CAPACITY AND CREW              (Network Flow)");
        System.out.println("   8. Maximum trains between two stations [Max Flow]");
        System.out.println("   9. Assign crew to trains             [Bipartite Matching]");
        System.out.println(" HARD PROBLEMS                  (NP-Complete + Approximation)");
        System.out.println("  10. Inspection tour of all stations   [TSP exact vs approx]");
        System.out.println("  11. Place ticket checkers             [Vertex Cover]");
        System.out.println(" RANDOMIZED AND PARALLEL");
        System.out.println("  12. Find the weakest link in network");
        System.out.println("  13. Sort trains by distance           [Randomized QuickSort]");
        System.out.println("  14. Speed test: normal vs parallel    [Fork/Join]");
        System.out.println(" TESTING");
        System.out.println("  15. Run all test cases");
        System.out.println("   0. Exit");
        System.out.println("--------------------------------------------------");
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number.");
            }
        }
    }

    static int readIntRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) return value;
            System.out.println("  Please enter a number from " + min + " to " + max + ".");
        }
    }

    static String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    static void printStations() {
        System.out.println("Stations:");
        for (int i = 0; i < Data.N; i++) System.out.println("  " + (i + 1) + ". " + Data.STATIONS[i]);
    }

    /** Asks for a station number (1..N) and returns its 0-based index. */
    static int askStation(String prompt) {
        while (true) {
            int x = readInt(prompt);
            if (x >= 1 && x <= Data.N) return x - 1;
            System.out.println("  Please choose a number from 1 to " + Data.N + ".");
        }
    }

    static String names(Collection<Integer> stationIndexes, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int s : stationIndexes) {
            if (sb.length() > 0) sb.append(separator);
            sb.append(Data.STATIONS[s]);
        }
        return sb.toString();
    }

    static Train findTrain(int number) {
        for (Train t : trains) if (t.number == number) return t;
        return null;
    }

    static int[] route(String... stationNames) {
        int[] r = new int[stationNames.length];
        for (int i = 0; i < r.length; i++) r[i] = Data.findStation(stationNames[i]);
        return r;
    }

    static void loadSampleTrains() {
        trains.add(new Train(12701, "Charminar Express", route("Hyderabad", "Vijayawada", "Chennai"), 900, 650));
        trains.add(new Train(12702, "Godavari Express", route("Hyderabad", "Warangal", "Vijayawada", "Visakhapatnam"), 850, 700));
        trains.add(new Train(12703, "Deccan Link", route("Hyderabad", "Bengaluru"), 700, 600));
        trains.add(new Train(12704, "Venkatadri Express", route("Vijayawada", "Chennai", "Tirupati"), 800, 550));
        trains.add(new Train(12705, "Nagpur Intercity", route("Hyderabad", "Warangal", "Nagpur"), 600, 500));
        trains.add(new Train(12706, "Southern Star", route("Visakhapatnam", "Vijayawada", "Chennai", "Bengaluru"), 750, 900));
    }

    // ------------------------------------------------------------------ 1-2: train management

    static void addTrain() {
        System.out.println("ADD A NEW TRAIN");
        int number = readInt("Train number: ");
        if (findTrain(number) != null) {
            System.out.println("A train with this number already exists.");
            return;
        }
        String name = readLine("Train name: ");
        if (name.isEmpty()) {
            System.out.println("Train name cannot be empty.");
            return;
        }
        String text = readLine("Route (station names separated by commas, e.g. Hyderabad, Warangal, Nagpur): ");
        String[] parts = text.split(",");
        if (parts.length < 2) {
            System.out.println("A route needs at least a source and a destination.");
            return;
        }
        int[] r = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            r[i] = Data.findStation(parts[i]);
            if (r[i] == -1) {
                int guess = StringAlgos.closestStation(parts[i]);
                System.out.println("Station '" + parts[i].trim() + "' is not in the network. Did you mean '"
                        + Data.STATIONS[guess] + "'?");
                return;
            }
            if (i > 0 && r[i] == r[i - 1]) {
                System.out.println("Two consecutive stops cannot be the same station.");
                return;
            }
        }
        int seats = readInt("Number of seats: ");
        int fare = readInt("Fare (Rs): ");
        if (seats <= 0 || fare <= 0) {
            System.out.println("Seats and fare must be positive numbers.");
            return;
        }
        Train t = new Train(number, name, r, seats, fare);
        trains.add(t);
        System.out.println("Train added: " + t.number + " " + t.name + " (" + t.routeText() + ", " + t.distance + " km)");
    }

    static void showTrains() {
        System.out.println("ALL TRAINS (" + trains.size() + ")");
        System.out.printf("%-7s %-20s %-48s %6s %6s %6s%n", "No.", "Name", "Route", "Km", "Seats", "Fare");
        for (Train t : trains) {
            System.out.printf("%-7d %-20s %-48s %6d %6d %6d%n", t.number, t.name, t.routeText(), t.distance, t.seats, t.fare);
        }
    }

    // ------------------------------------------------------------------ 3-5: string algorithms

    static void searchTrains() {
        System.out.println("SEARCH TRAINS BY KEYWORD (KMP)");
        String keyword = readLine("Enter a word from a train name or station: ").toLowerCase();
        if (keyword.isEmpty()) {
            System.out.println("Please type a keyword.");
            return;
        }
        int count = 0;
        for (Train t : trains) {
            String text = (t.name + " " + t.routeText()).toLowerCase();
            List<Integer> hits = StringAlgos.kmpSearch(text, keyword);
            if (!hits.isEmpty()) {
                count++;
                System.out.println("  " + t.number + " " + t.name + "  [" + t.routeText() + "]  - found at position " + hits.get(0));
            }
        }
        if (count == 0) System.out.println("  No train matches '" + keyword + "'.");
        System.out.println("\nWhy KMP: it never re-reads text it has already matched, so searching takes O(n + m).");
    }

    static void spellCheckStation() {
        System.out.println("STATION NAME SPELL-CHECKER (Edit Distance)");
        String typed = readLine("Type a station name (try a spelling mistake): ");
        if (typed.isEmpty()) {
            System.out.println("Please type a name.");
            return;
        }
        if (Data.findStation(typed) != -1) {
            System.out.println("'" + typed + "' is a valid station.");
        } else {
            int best = StringAlgos.closestStation(typed);
            int d = StringAlgos.editDistance(typed, Data.STATIONS[best]);
            if (d <= 4) {
                System.out.println("Not found. Did you mean '" + Data.STATIONS[best] + "'? (" + d + " change(s) needed)");
            } else {
                System.out.println("Not found, and no station name is close to it.");
            }
        }
        System.out.println("\nWhy DP: edit distance fills a table of the cheapest way to convert prefixes, O(n x m).");
    }

    static void commonStops() {
        System.out.println("COMMON STOPS OF TWO TRAINS (Longest Common Subsequence)");
        showTrains();
        Train a = findTrain(readInt("\nFirst train number: "));
        Train b = findTrain(readInt("Second train number: "));
        if (a == null || b == null) {
            System.out.println("One of the train numbers does not exist.");
            return;
        }
        List<String> common = StringAlgos.lcs(a.routeNames(), b.routeNames());
        if (common.isEmpty()) System.out.println("These two trains have no stop in common.");
        else System.out.println("Common stops, in travel order: " + String.join(" > ", common) + "  (" + common.size() + " stops)");
        System.out.println("\nWhy LCS: it finds the longest list of stops both trains visit in the same order, O(n x m).");
    }

    // ------------------------------------------------------------------ 6-7: dynamic programming

    static void shortestDistance() {
        System.out.println("SHORTEST DISTANCE BETWEEN STATIONS (Floyd-Warshall)");
        printStations();
        int from = askStation("From station number: ");
        int to = askStation("To station number: ");
        if (from == to) {
            System.out.println("Both stations are the same - distance is 0 km.");
            return;
        }
        System.out.println("Shortest route: " + names(DPAlgos.path(from, to), " > "));
        System.out.println("Distance: " + DPAlgos.DIST[from][to] + " km");
        System.out.println("\nWhy Floyd-Warshall: it computes every pair of stations once at startup in O(V^3),");
        System.out.println("so each question afterwards is answered instantly.");
    }

    static void loadCargo() {
        System.out.println("LOAD CARGO INTO A WAGON (0/1 Knapsack)");
        System.out.printf("%-14s %12s %16s%n", "Cargo", "Weight (t)", "Value (Rs '000)");
        for (int i = 0; i < Data.CARGO_NAMES.length; i++) {
            System.out.printf("%-14s %12d %16d%n", Data.CARGO_NAMES[i], Data.CARGO_WEIGHT[i], Data.CARGO_VALUE[i]);
        }
        int cap = readInt("\nWagon capacity in tonnes (e.g. 60): ");
        if (cap < 0) {
            System.out.println("Capacity cannot be negative.");
            return;
        }
        List<Integer> chosen = new ArrayList<>();
        int best = DPAlgos.knapsack(Data.CARGO_WEIGHT, Data.CARGO_VALUE, cap, chosen);
        Collections.reverse(chosen);
        int weight = 0;
        System.out.println("Best load:");
        for (int i : chosen) {
            System.out.println("  " + Data.CARGO_NAMES[i] + " (" + Data.CARGO_WEIGHT[i] + " t)");
            weight += Data.CARGO_WEIGHT[i];
        }
        if (chosen.isEmpty()) System.out.println("  Nothing fits in this wagon.");
        System.out.println("Total weight: " + weight + " t, total value: Rs " + best + ",000");
        System.out.println("\nWhy DP: each cargo is either taken or left, and the table remembers the best value");
        System.out.println("for every capacity, O(items x capacity). Greedy 'heaviest value first' can be wrong.");
    }

    // ------------------------------------------------------------------ 8-9: network flow

    static void maxTrains() {
        System.out.println("MAXIMUM TRAINS BETWEEN TWO STATIONS (Max Flow)");
        printStations();
        int s = askStation("From station number: ");
        int t = askStation("To station number: ");
        if (s == t) {
            System.out.println("Please choose two different stations.");
            return;
        }
        List<String> log = new ArrayList<>();
        int flow = FlowAlgos.maxFlow(FlowAlgos.trackCapacities(), s, t, log, Data.STATIONS);
        System.out.println("Routes used:");
        for (String line : log) System.out.println("  " + line);
        System.out.println("Maximum trains per day from " + Data.STATIONS[s] + " to " + Data.STATIONS[t] + ": " + flow);
        System.out.println("\nWhy Edmonds-Karp: it keeps finding the shortest route with spare track capacity,");
        System.out.println("O(V x E^2). The answer equals the smallest set of tracks that separates the two stations.");
    }

    static void assignCrew() {
        System.out.println("ASSIGN CREW TO TRAINS (Interactive Bipartite Matching)");
        System.out.println("Rule: a crew member can drive a train that starts or ends at their home station.");
        System.out.println("\nAvailable crew:");
        for (int c = 0; c < Data.CREW_NAMES.length; c++) {
            System.out.println("  " + (c + 1) + ". " + Data.CREW_NAMES[c] + " (" + Data.STATIONS[Data.CREW_HOME[c]] + ")");
        }
        if (trains.isEmpty()) {
            System.out.println("No trains are available to assign.");
            return;
        }

        int[] assignedCrew = new int[trains.size()];
        Arrays.fill(assignedCrew, -1);
        boolean[] busy = new boolean[Data.CREW_NAMES.length];
        System.out.println("\nTrains:");
        for (int i = 0; i < trains.size(); i++) {
            Train t = trains.get(i);
            System.out.println("  " + t.number + " " + t.name + " (" + Data.STATIONS[t.source()] + " to " + Data.STATIONS[t.destination()] + ")");
        }
        System.out.println("\nEnter a train number, then a crew number to assign them.");
        System.out.println("Enter 0 for a train number when you are done.");
        while (true) {
            int trainNumber = readInt("Train number (0 to finish): ");
            if (trainNumber == 0) break;
            int trainIndex = -1;
            for (int i = 0; i < trains.size(); i++) {
                if (trains.get(i).number == trainNumber) { trainIndex = i; break; }
            }
            if (trainIndex < 0) {
                System.out.println("  No train has number " + trainNumber + ".");
                continue;
            }
            if (assignedCrew[trainIndex] >= 0) {
                System.out.println("  That train already has a crew member assigned.");
                continue;
            }

            Train train = trains.get(trainIndex);
            List<Integer> eligible = new ArrayList<>();
            System.out.println("Eligible crew for " + train.name + ":");
            for (int c = 0; c < Data.CREW_NAMES.length; c++) {
                int home = Data.CREW_HOME[c];
                if (home == train.source() || home == train.destination()) {
                    eligible.add(c);
                    System.out.println("  " + (c + 1) + ". " + Data.CREW_NAMES[c] + " (" + Data.STATIONS[home] + ")" + (busy[c] ? " - already assigned" : ""));
                }
            }
            int crewNumber = readInt("Crew number (0 to skip this train): ");
            if (crewNumber == 0) continue;
            if (crewNumber < 1 || crewNumber > Data.CREW_NAMES.length || !eligible.contains(crewNumber - 1)) {
                System.out.println("  That crew member is not eligible for this train.");
                continue;
            }
            int crewIndex = crewNumber - 1;
            if (busy[crewIndex]) {
                System.out.println("  That crew member is already assigned to another train.");
                continue;
            }
            assignedCrew[trainIndex] = crewIndex;
            busy[crewIndex] = true;
            System.out.println("  Assigned " + Data.CREW_NAMES[crewIndex] + " to " + train.name + ".");
        }

        int assigned = 0;
        System.out.println("\nFinal assignment:");
        for (int i = 0; i < trains.size(); i++) {
            Train t = trains.get(i);
            if (assignedCrew[i] >= 0) {
                assigned++;
                System.out.println("  " + t.number + " " + t.name + " -> " + Data.CREW_NAMES[assignedCrew[i]]);
            } else System.out.println("  " + t.number + " " + t.name + " -> NOT ASSIGNED");
        }
        System.out.println(assigned + " of " + trains.size() + " trains have a driver.");
        for (int c = 0; c < busy.length; c++) {
            if (!busy[c]) System.out.println("Unused crew member: " + Data.CREW_NAMES[c]);
        }
        System.out.println("Each crew member and each train can be assigned at most once.");
    }

    // ------------------------------------------------------------------ 10-11: NP-complete and approximation

    static void inspectionTour() {
        System.out.println("INSPECTION TOUR OF ALL STATIONS (Travelling Salesman)");
        printStations();
        int start = askStation("Choose the starting station number: ");
        String startName = Data.STATIONS[start];
        System.out.println("The engineer will start and finish at " + startName + ", visiting every station once.");
        List<Integer> exactTour = new ArrayList<>();
        List<Integer> approxTour = new ArrayList<>();
        long t0 = System.nanoTime();
        int exact = DPAlgos.tspExact(DPAlgos.DIST, start, exactTour);
        long t1 = System.nanoTime();
        int approx = NPAlgos.tspApprox(DPAlgos.DIST, start, approxTour);
        long t2 = System.nanoTime();
        System.out.println("\nBest tour (exact, bitmask DP):  " + names(exactTour, " > ") + " > " + startName);
        System.out.println("  Length: " + exact + " km   (" + (t1 - t0) / 1000 + " microseconds)");
        System.out.println("Quick tour (approximation):     " + names(approxTour, " > ") + " > " + startName);
        System.out.println("  Length: " + approx + " km   (" + (t2 - t1) / 1000 + " microseconds)");
        System.out.printf("The quick tour is %.2f times the best tour (guarantee: at most 2.00).%n", (double) approx / exact);
        System.out.println("\nTSP is NP-Complete. The exact DP takes O(2^n x n^2) - fine for 8 stations, impossible for 50.");
        System.out.println("The approximation (minimum spanning tree + preorder walk) takes O(n^2) and is never worse than 2x.");
    }

    static void placeCheckers() {
        System.out.println("PLACE TICKET CHECKERS (Vertex Cover)");
        System.out.println("Goal: use the fewest stations so that every track has a checker at one of its ends.");
        System.out.println("Choose a result: 1. Exact answer  2. Quick approximation  3. Compare both");
        int choice = readIntRange("Your choice: ", 1, 3);
        int[][] edges = Data.edgePairs();
        if (choice == 1 || choice == 3) {
            Set<Integer> exact = NPAlgos.vertexCoverExact(Data.N, edges);
            System.out.println("\nBest answer (exact): " + exact.size() + " stations -> " + names(exact, ", "));
            System.out.println("All tracks covered: " + NPAlgos.isCover(edges, exact));
        }
        if (choice == 2 || choice == 3) {
            Set<Integer> approx = NPAlgos.vertexCoverApprox(edges);
            System.out.println("\nQuick answer (approximation): " + approx.size() + " stations -> " + names(approx, ", "));
            System.out.println("All tracks covered: " + NPAlgos.isCover(edges, approx));
        }
        System.out.println("\nVertex Cover is NP-Complete. The quick method picks both ends of an uncovered track,");
        System.out.println("so it uses at most 2x the best number of checkers, in O(E) time.");
    }

    // ------------------------------------------------------------------ 12-14: randomized and parallel

    static void weakestLink() {
        System.out.println("WEAKEST LINK IN THE NETWORK (Karger's Randomized Min-Cut)");
        int trials = readIntRange("Number of randomized trials (1-10000): ", 1, 10000);
        RandomParallel.CutResult r = RandomParallel.kargerMinCut(Data.N, Data.edgePairs(), trials, rnd);
        List<Integer> sideA = new ArrayList<>(), sideB = new ArrayList<>();
        for (int i = 0; i < Data.N; i++) (r.sideA[i] ? sideA : sideB).add(i);
        System.out.println("Fewest tracks whose failure splits the network: " + r.size);
        for (int[] e : r.crossing) {
            System.out.println("  Track " + Data.STATIONS[e[0]] + " - " + Data.STATIONS[e[1]]);
        }
        System.out.println("Group 1: " + names(sideA, ", "));
        System.out.println("Group 2: " + names(sideB, ", "));
        int n = Data.N;
        double fail = Math.pow(1.0 - 2.0 / (n * (n - 1.0)), trials);
        System.out.printf("%nTrials run: %d. Chance that the answer is not the true minimum: about %.4f%%.%n", trials, fail * 100);
        System.out.println("\nWhy randomized: one trial merges random tracks until two groups remain. It finds the true");
        System.out.println("minimum with probability >= 2/(n(n-1)); repeating the trial makes a miss very unlikely.");
    }

    static void sortTrains() {
        System.out.println("SORT TRAINS BY DISTANCE (Randomized QuickSort)");
        System.out.println("Sort order: 1. Shortest to longest  2. Longest to shortest");
        int order = readIntRange("Choose sort order: ", 1, 2);
        Train[] arr = trains.toArray(new Train[0]);
        Comparator<Train> byDistance = Comparator.comparingInt((Train t) -> t.distance);
        if (order == 2) byDistance = byDistance.reversed();
        RandomParallel.randomizedQuickSort(arr, byDistance, rnd);
        for (Train t : arr) System.out.printf("  %-7d %-20s %5d km%n", t.number, t.name, t.distance);

        int size = readIntRange("\nExperiment size (1-100000): ", 1, 100000);
        System.out.println("Small experiment: sorting " + size + " numbers that are ALREADY in order");
        Integer[] a = new Integer[size], b = new Integer[size];
        for (int i = 0; i < a.length; i++) a[i] = b[i] = i;
        long t0 = System.nanoTime();
        RandomParallel.firstPivotQuickSort(a, Comparator.naturalOrder());
        long t1 = System.nanoTime();
        RandomParallel.randomizedQuickSort(b, Comparator.naturalOrder(), rnd);
        long t2 = System.nanoTime();
        System.out.println("  First-element pivot: " + (t1 - t0) / 1_000_000.0 + " ms   (worst case, O(n^2))");
        System.out.println("  Random pivot:        " + (t2 - t1) / 1_000_000.0 + " ms   (expected O(n log n))");
        System.out.println("\nWhy randomized: a random pivot means no particular input can always be slow.");
    }

    static void speedTest() {
        System.out.println("SPEED TEST: NORMAL vs PARALLEL (Fork/Join)");
        int fareCount = readIntRange("Number of ticket fares (1-5000000): ", 1, 5_000_000);
        int bookingCount = readIntRange("Number of booking IDs (1-2000000): ", 1, 2_000_000);
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU cores available: " + cores);
        System.out.println("Creating " + fareCount + " ticket fares and " + bookingCount + " booking IDs... (please wait)");

        int[] fares = new int[fareCount];
        int[] ids = new int[bookingCount];
        for (int i = 0; i < fares.length; i++) fares[i] = 100 + rnd.nextInt(1900);
        for (int i = 0; i < ids.length; i++) ids[i] = rnd.nextInt();

        // warm-up: run both versions a few times first so Java's JIT compiler has optimised them before we time them
        int[] warmFares = Arrays.copyOf(fares, Math.min(fareCount, 100_000));
        int[] warmIds = Arrays.copyOf(ids, Math.min(bookingCount, 100_000));
        for (int round = 0; round < 2; round++) {
            RandomParallel.sequentialRevenue(warmFares);
            RandomParallel.parallelRevenue(warmFares);
            RandomParallel.sequentialSort(warmIds.clone());
            RandomParallel.parallelSort(warmIds.clone());
        }

        long t0 = System.nanoTime();
        long s1 = RandomParallel.sequentialRevenue(fares);
        long t1 = System.nanoTime();
        long s2 = RandomParallel.parallelRevenue(fares);
        long t2 = System.nanoTime();
        int[] x = ids.clone(), y = ids.clone();
        long t3 = System.nanoTime();
        RandomParallel.sequentialSort(x);
        long t4 = System.nanoTime();
        RandomParallel.parallelSort(y);
        long t5 = System.nanoTime();

        double seqSum = (t1 - t0) / 1e6, parSum = (t2 - t1) / 1e6;
        double seqSort = (t4 - t3) / 1e6, parSort = (t5 - t4) / 1e6;
        System.out.println("\nNet revenue of " + fareCount + " tickets (fare + GST - discount formula, one calculation per ticket):");
        System.out.printf("  Normal:   %8.2f ms  (Rs %,d)%n", seqSum, s1 / 100);
        System.out.printf("  Parallel: %8.2f ms  (Rs %,d)  speed-up %.2fx%n", parSum, s2 / 100, seqSum / Math.max(parSum, 0.001));
        System.out.println("Sorting " + bookingCount + " booking IDs (merge sort):");
        System.out.printf("  Normal:   %8.2f ms%n", seqSort);
        System.out.printf("  Parallel: %8.2f ms  speed-up %.2fx%n", parSort, seqSort / Math.max(parSort, 0.001));
        System.out.println("Results identical: " + (s1 == s2 && Arrays.equals(x, y)));
        System.out.println("\nWhy parallel: the array is split in halves, each half is handled by a different core,");
        System.out.println("then the answers are combined. Speed-up depends on your core count; with 1 core it is about 1x.");
    }
}
