# RailSync - Intelligent Railway Operations Platform

Project document: what every menu option does, why it uses its algorithm, and how it works (DSA-3 Final Project)

## 1. Problem statement and objectives

Railway operations teams repeatedly face the same questions: Which trains serve a station? What is the shortest way between two stations? How many trains can the tracks carry? Which driver can take which train? What is the cheapest way to inspect every station? Which track failure would hurt the network most? Answering these by hand is slow and error-prone.

**RailSync** is a Java console application that answers these questions with the algorithms taught in DSA-3. Its objectives are:

- Keep a simple register of trains (add a new train, show all trains).
- Solve each operational problem with the most suitable DSA-3 algorithm, and explain why that algorithm was chosen.
- Show results in plain language so that a non-programmer can read them.
- Prove correctness with built-in test cases and measure performance.

## 2. Architecture

The program is split into small files so that each DSA-3 module lives in its own class.

- **Railsync.java** - the numbered menu and all user input/output.
- **Data.java** - sample network: 8 stations, 13 tracks (distance and trains-per-day capacity), 6 crew members, 6 cargo types.
- **Train.java** - one train record (number, name, route, seats, fare, total distance).
- **StringAlgos.java** - KMP, Edit Distance, Longest Common Subsequence.
- **DPAlgos.java** - Floyd-Warshall, 0/1 Knapsack, exact TSP (Held-Karp).
- **FlowAlgos.java** - Edmonds-Karp maximum flow and bipartite matching.
- **NPAlgos.java** - Vertex Cover (exact and 2-approximation) and TSP 2-approximation.
- **RandomParallel.java** - Randomized QuickSort, Karger min-cut, Fork/Join parallel revenue total and merge sort.
- **Tests.java** - 31 automated test cases (menu option 15).

The stations are Hyderabad, Warangal, Vijayawada, Visakhapatnam, Nagpur, Chennai, Tirupati and Bengaluru. Distances and capacities are approximate sample values for demonstration.

## 3. DSA-3 syllabus mapping

- **String algorithms** - menu options 3, 4, 5 (KMP, Edit Distance, LCS).
- **Advanced Dynamic Programming** - options 4, 5, 6, 7, 10 (Edit Distance, LCS, Floyd-Warshall, Knapsack, Held-Karp TSP).
- **Network flow** - options 8, 9 (Edmonds-Karp, bipartite matching).
- **NP-completeness and approximation** - options 10, 11 (TSP and Vertex Cover, exact versus approximate).
- **Randomized algorithms** - options 12, 13 (Karger min-cut, Randomized QuickSort).
- **Parallel algorithms** - option 14 (Fork/Join).
- **Testing and performance evaluation** - option 15, plus the timings printed by options 10, 13 and 14.

## 4. Menu options explained

How to start: compile with `javac -d ../out *.java` inside the `src` folder, then run `java Railsync` from the `out` folder. Type a number and press Enter. After each option, press Enter to return to the menu.

### Option 1 - Add a new train

**What it does:** Registers a new train. The program asks for the train number, name, route (station names separated by commas, first = source, last = destination), number of seats and fare.

**How it works:** Each station name is checked against the network. If a name is wrong, the program suggests the closest station using Edit Distance (see option 4) instead of silently failing. The total distance is computed by adding the shortest distance (option 6) between each pair of consecutive stops, so a train may use stops that are not directly connected.

**Example:** Train 12707, "Rayalaseema Express", route "Hyderabad, Warangal, Chennai", 700 seats, Rs 450 is added with distance 800 km. Entering "Hydrabad" gives: Did you mean 'Hyderabad'?

**Checks done:** duplicate train number, empty name, fewer than two stations, repeated consecutive station, non-positive seats or fare.

### Option 2 - Show all trains

**What it does:** Prints a table of every train: number, name, route, distance in km, seats and fare. Trains added in option 1 appear here immediately. It is a simple loop over the list, O(T) for T trains.

### Option 3 - Search trains by keyword (KMP)

**What it does:** The user types a word (for example "chennai", "express" or "godavari"). Every train whose name or route contains that word is listed.

**Why KMP:** A naive search may compare the same characters again and again. KMP builds a failure table (the longest prefix of the keyword that is also a suffix) so that after a mismatch it never moves backwards in the text. Searching takes O(n + m) where n is the text length and m the keyword length.

**How it works:** For each train, the name and route are joined into one lower-case text. The keyword's failure table is built once, then the text is scanned left to right while a pointer j tracks how many keyword characters currently match. When j equals the keyword length, a match is reported.

**Example:** Keyword "chennai" lists 12701 Charminar Express, 12704 Venkatadri Express and 12706 Southern Star (and any new train that passes through Chennai).

### Option 4 - Station name spell-checker (Edit Distance)

**What it does:** The user types a station name. If it is valid the program says so; otherwise it suggests the closest station and tells how many changes are needed.

**Why Edit Distance:** It measures how different two words are as the smallest number of insertions, deletions and replacements needed to turn one into the other. It is a Dynamic Programming problem: dp[i][j] is the cheapest way to convert the first i letters of one word into the first j letters of the other. The table is filled in O(n x m).

**How it works:** The typed word is compared with all 8 station names and the one with the smallest distance wins. If the best distance is more than 4, the program reports that nothing is close.

**Example:** "Visakapatnam" gives: Did you mean 'Visakhapatnam'? (1 change needed).

### Option 5 - Common stops of two trains (LCS)

**What it does:** After choosing two train numbers, the program prints the stops both trains visit in the same travel order.

**Why LCS:** The Longest Common Subsequence of two lists is the longest list of items that appear in both in the same order, though not necessarily next to each other. Dynamic Programming fills a table where dp[i][j] is the LCS length of the first i stops of train A and the first j stops of train B, O(n x m). The answer is recovered by walking the table backwards.

**Use case:** Planning connections and spotting overlapping services.

**Example:** Trains 12701 (Hyderabad > Vijayawada > Chennai) and 12706 (Visakhapatnam > Vijayawada > Chennai > Bengaluru) share Vijayawada > Chennai.

### Option 6 - Shortest distance between stations (Floyd-Warshall)

**What it does:** The user picks two stations by number. The program prints the shortest route and its length in km.

**Why Floyd-Warshall:** It finds the shortest distance between every pair of stations. The Dynamic Programming idea is to allow one more station k as a possible middle stop at a time: the best way from i to j is either the old way or i to k to j. It runs once at startup in O(V^3); each question afterwards is answered by a table lookup. A second table remembers the next station on each route so the full path can be printed.

**Example:** Hyderabad to Chennai: Hyderabad > Vijayawada > Chennai, 700 km (the alternative through Bengaluru is 920 km).

### Option 7 - Load cargo into a wagon (0/1 Knapsack)

**What it does:** A table of cargo types (weight in tonnes, value in thousand rupees) is shown. The user enters the wagon capacity, and the program chooses the most valuable load that fits.

**Why Knapsack:** Each cargo is either loaded completely or left out. Choosing the highest value-per-tonne item first (greedy) can give a worse answer, so Dynamic Programming is used: dp[i][c] is the best value using the first i cargos with capacity c. Time and memory are O(items x capacity). The chosen cargos are found by walking the table backwards.

**Example:** Capacity 60 t gives Medicines (10 t), Electronics (25 t) and Fruits (20 t): weight 55 t, value Rs 300,000.

### Option 8 - Maximum trains between two stations (Max Flow)

**What it does:** The user picks a start and an end station. The program prints how many trains per day the tracks can carry between them and which routes carry them.

**Why Max Flow:** Each track has a capacity (trains per day). The maximum flow is the largest number of trains that can travel at the same time without exceeding any track's capacity. The Edmonds-Karp method repeatedly finds the shortest route (by number of tracks) that still has spare capacity using BFS, sends as much as possible along it, updates the remaining capacities (including reverse capacity so earlier decisions can be undone) and stops when no such route exists. Time O(V x E^2).

**Meaning of the answer:** By the max-flow min-cut theorem, the answer equals the smallest total capacity of tracks whose removal would cut the two stations apart, so it also identifies the bottleneck.

**Example:** Hyderabad to Chennai: 18 trains per day (8 via Vijayawada, 6 via Bengaluru, 4 via Warangal > Vijayawada > Tirupati).

### Option 9 - Assign crew to trains (Bipartite Matching)

**What it does:** Assigns each train a different driver. Rule: a crew member can take a train that starts or ends at their home station.

**Why matching through flow:** Crew on one side and trains on the other form a bipartite graph. Adding a source (linked to every crew member) and a sink (linked from every train), with capacity 1 on every link, turns the problem into Max Flow: each unit of flow is one crew-to-train assignment, and the maximum flow is the largest possible number of assignments.

**Example:** With the six sample trains, five get a driver and Southern Star (Visakhapatnam > Bengaluru) is left without one, because Kiran is the only crew member who can drive it but is also needed for Deccan Link (Ravi and Anita are fully used by Godavari Express and Nagpur Intercity). One of those two trains must stay without a driver; in the sample run Kiran is given to Deccan Link. Latha is unused. This shows a real shortage the manager must fix.

### Option 10 - Inspection tour of all stations (TSP exact versus approximate)

**What it does:** An engineer starts at Hyderabad, visits every station exactly once and returns. The program prints the best possible tour and a quick approximate tour and compares them.

**Why two methods:** The Travelling Salesman Problem is NP-Complete, so no known algorithm is fast for every input size.

- Exact: Held-Karp Dynamic Programming over subsets (bitmask). dp[mask][last] is the shortest way to visit the stations in mask ending at last. Time O(2^n x n^2): fine for 8 stations, impossible for 50.
- Approximate: build a Minimum Spanning Tree (Prim's algorithm) and visit stations in the order a depth-first walk meets them. Because distances obey the triangle inequality, the tour is never longer than 2 times the optimum. Time O(n^2).

**Example:** Exact tour 3110 km: Hyderabad > Bengaluru > Tirupati > Chennai > Vijayawada > Visakhapatnam > Nagpur > Warangal > Hyderabad. Approximate tour 3530 km, which is 1.14 times the best (the guarantee is at most 2.00).

### Option 11 - Place ticket checkers (Vertex Cover)

**What it does:** Finds the fewest stations to put a ticket checker at so that every track has a checker at one of its two ends.

**Why two methods:** Vertex Cover is NP-Complete. The exact method tries all 2^8 subsets of stations and keeps the smallest one that covers every track. The approximation repeatedly takes any uncovered track and adds both of its stations. The tracks chosen this way share no station, and any valid answer needs at least one station from each of them, so the approximation uses at most twice the minimum. Time O(E).

**Example:** Approximation: 6 stations (Hyderabad, Warangal, Vijayawada, Visakhapatnam, Chennai, Tirupati). Best: 5 stations (Hyderabad, Warangal, Visakhapatnam, Chennai, Tirupati). Both cover all 13 tracks.

### Option 12 - Find the weakest link in the network (Karger's randomized min-cut)

**What it does:** Finds the smallest number of tracks whose failure would split the network into two disconnected parts, and shows those tracks and the two groups of stations.

**Why randomized:** In Karger's algorithm, the tracks are shuffled and stations are merged along them until only two groups remain; the tracks left between the two groups form a cut. One attempt finds a true minimum cut with probability at least 2 / (n(n-1)), which is small for a single try, so the program repeats the attempt 200 times and keeps the smallest cut. The chance that all 200 attempts miss the true minimum is about 0.07 percent for 8 stations, and the program prints this figure. Merging stations uses a union-find structure.

**Example:** The weakest link has 2 tracks: Vijayawada - Visakhapatnam and Visakhapatnam - Nagpur. If both fail, Visakhapatnam is cut off from the rest of the network.

### Option 13 - Sort trains by distance (Randomized QuickSort)

**What it does:** Prints all trains sorted from the shortest to the longest distance, then runs a small experiment on 5000 numbers that are already sorted.

**Why randomized:** QuickSort that always picks the first element as the pivot takes O(n^2) time on sorted input, because every split is as lopsided as possible. Picking the pivot at random makes this very unlikely for any input, giving expected O(n log n) time.

**Example:** On this experiment the random-pivot version takes about 8 ms against about 80 ms for the first-element pivot (times vary by computer).

### Option 14 - Speed test: normal versus parallel (Fork/Join)

**What it does:** Creates 10,000,000 ticket fares and 2,000,000 booking IDs, then times two jobs each in a normal and in a parallel version: (1) the net revenue of all tickets (one calculation per ticket: fare plus GST minus a discount formula) and (2) sorting the booking IDs with merge sort. It confirms both versions give identical results.

**How the parallel version works:** The Fork/Join framework splits the array in halves again and again until each piece is small (50,000 items), processes the small pieces on different CPU cores, and combines the answers (adding two totals, or merging two sorted halves). With p cores the work takes about O(n / p + log n) time.

**Reading the result:** The speed-up depends on how many CPU cores your computer has. On a single core the two versions take about the same time (speed-up near 1x). On a multi-core computer the revenue calculation should speed up noticeably; merge sort improves less because the final merge steps are sequential.

### Option 15 - Run all test cases

**What it does:** Runs 31 automatic checks and prints [PASS] or [FAIL] for each, followed by a total.

**What is checked:**

- Strings: KMP positions and failure table, edit distance kitten to sitting = 3, spell suggestion, LCS.
- Dynamic Programming: shortest distances and paths, knapsack textbook case (answer 220), knapsack with capacity 0, train distance calculation.
- Flow: the standard textbook network (answer 23), the track network compared with a brute-force minimum cut, matching cases with all and with only one possible assignment.
- NP and approximation: tours visit every station once, exact is not longer than approximate, approximate is within 2 times the exact; vertex covers really cover every track and the approximation is within 2 times the exact.
- Randomized and parallel: Karger agrees with a brute-force minimum cut, Randomized QuickSort sorts random and already-sorted input, parallel revenue and parallel merge sort equal the sequential results.

The latest output is saved in tests/test-results.txt.

### Option 0 - Exit

Ends the program.

## 5. Complexity summary

- KMP search: O(n + m)
- Edit Distance and LCS: O(n x m)
- Floyd-Warshall: O(V^3)
- 0/1 Knapsack: O(items x capacity)
- Edmonds-Karp and matching: O(V x E^2)
- Held-Karp TSP: O(2^n x n^2); TSP approximation: O(n^2) with ratio at most 2
- Vertex Cover exact: O(2^n x E); approximation: O(E) with ratio at most 2
- Karger min-cut: O(trials x E), success probability at least 2 / (n(n-1)) per trial
- Randomized QuickSort: expected O(n log n)
- Parallel merge sort and sum: about O(n / p + log n) with p cores

## 6. Questions you may be asked in the viva

- **Why KMP and not a plain search?** It never moves backwards in the text, so it is O(n + m) instead of O(n x m).
- **Why does Floyd-Warshall use the loop order k, i, j?** k is the allowed middle station; it must be the outermost loop so that all paths using stations 0..k-1 are already complete before station k is added.
- **Why can greedy fail for knapsack?** Taking the best value-per-tonne cargo first can leave unused space that a different combination would have filled.
- **What is the max-flow min-cut theorem?** The maximum flow equals the capacity of the smallest set of tracks that separates the source from the sink; option 8 and its tests rely on it.
- **Why is the TSP approximation at most twice the optimum?** A tour minus one edge is a spanning tree, so the MST weight is at most the optimum; walking the tree visits each edge twice, and shortcuts never increase the length when distances obey the triangle inequality.
- **Why is the Vertex Cover approximation at most twice the optimum?** The chosen tracks share no station, so any valid cover needs a different station for each one, while the approximation takes exactly two per chosen track.
- **Is Karger's algorithm always correct?** No - it is a Monte Carlo algorithm. A single run can miss the minimum, but repeating it makes a miss extremely unlikely, and the program prints that probability.
- **Why might parallel code not be faster?** Starting and combining tasks has a cost, memory speed can limit simple loops, and a computer with one core cannot run two pieces at the same time.

## 7. Limits of the project

The network, crew and cargo data are fixed sample values in Data.java. The exact TSP and Vertex Cover methods are meant for small networks (about 20 stations at most) because NP-complete problems grow exponentially. Trains are kept in memory only and are lost when the program closes.
