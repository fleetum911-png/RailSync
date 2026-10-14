# RailSync - Intelligent Railway Operations Platform

A menu-driven Java console application for the **DSA-3** course project. RailSync manages trains and
solves real railway-operations problems (search, routing, cargo loading, crew assignment, inspection tours,
network reliability) using the algorithms taught in DSA-3.

No external libraries are needed - only Java 11 or newer.

## How to run

```bash
cd src
javac -d ../out *.java
cd ../out
java Railsync
```

Type a number from the menu and press Enter. Option `15` runs every test case; option `0` exits.

## Menu and algorithm map

| # | Menu option | Algorithm | DSA-3 module | Time complexity |
|---|-------------|-----------|--------------|-----------------|
| 1 | Add a new train | Input validation + spell-suggestion | Strings, DP | O(L x n x m) for suggestion |
| 2 | Show all trains | - | - | O(T) |
| 3 | Search trains by keyword | KMP pattern search | String algorithms | O(n + m) |
| 4 | Station name spell-checker | Edit Distance | String algorithms + DP | O(n x m) |
| 5 | Common stops of two trains | Longest Common Subsequence | String algorithms + DP | O(n x m) |
| 6 | Shortest distance between stations | Floyd-Warshall | Advanced DP | O(V^3) |
| 7 | Load cargo into a wagon | 0/1 Knapsack | Advanced DP | O(items x capacity) |
| 8 | Maximum trains between two stations | Edmonds-Karp Max Flow | Network flow | O(V x E^2) |
| 9 | Assign crew to trains | Bipartite matching via Max Flow | Network flow | O(V x E^2) |
| 10 | Inspection tour of all stations | TSP: exact (Held-Karp DP) vs 2-approximation (MST) | NP-completeness + approximation | O(2^n n^2) vs O(n^2) |
| 11 | Place ticket checkers | Vertex Cover: exact vs 2-approximation | NP-completeness + approximation | O(2^n E) vs O(E) |
| 12 | Find the weakest link in the network | Karger randomized min-cut | Randomized algorithms | O(trials x E) |
| 13 | Sort trains by distance | Randomized QuickSort (vs first-pivot) | Randomized algorithms | O(n log n) expected |
| 14 | Speed test: normal vs parallel | Fork/Join parallel sum and merge sort | Parallel algorithms | O(n / p + log n) |
| 15 | Run all test cases | 31 automated checks | Testing | - |

## Repository structure

```
RailSync/
|-- README.md
|-- .gitignore
|-- src/
|   |-- Railsync.java        main menu and user interaction
|   |-- Data.java            sample railway network, crew and cargo data
|   |-- Train.java           train record
|   |-- StringAlgos.java     KMP, Edit Distance, LCS
|   |-- DPAlgos.java         Floyd-Warshall, Knapsack, Held-Karp TSP
|   |-- FlowAlgos.java       Edmonds-Karp, bipartite matching
|   |-- NPAlgos.java         Vertex Cover and TSP approximations
|   |-- RandomParallel.java  randomized QuickSort, Karger, Fork/Join
|   `-- Tests.java           31 built-in test cases
|-- tests/
|   `-- test-results.txt     output of menu option 15
`-- docs/
    `-- MENU_GUIDE.md        explanation of every menu option
```

## Testing

Menu option 15 runs 31 test cases covering every module. Each result is printed as `[PASS]` or `[FAIL]`.
Where a textbook answer exists it is used (e.g. edit distance kitten -> sitting = 3, max-flow example = 23,
knapsack = 220). Flow, min-cut, vertex-cover and TSP results are also checked against brute force.
The latest output is stored in `tests/test-results.txt`.

## Performance evaluation

* Option 10 prints the running time and tour length of the exact and approximate TSP methods.
* Option 13 times first-pivot QuickSort against Randomized QuickSort on already-sorted input.
* Option 14 times normal and parallel versions of a revenue calculation and merge sort on millions of records.
  Speed-up depends on the number of CPU cores; with a single core it is about 1x.

## Notes on the sample data

Stations, distances, track capacities, crew homes and cargo values in `Data.java` are approximate sample data
for demonstration. Change them there to model a different network.

## Team contributions

| Member | Roll no. | Modules / files owned |
|--------|----------|-----------------------|
| _name_ | _roll_ | _e.g. StringAlgos.java, menu options 3-5_ |
| _name_ | _roll_ | _e.g. DPAlgos.java, menu options 6-7_ |
| _name_ | _roll_ | _e.g. FlowAlgos.java, NPAlgos.java, menu options 8-11_ |
| _name_ | _roll_ | _e.g. RandomParallel.java, Tests.java, menu options 12-15_ |


# RailSync - Intelligent Railway Operations Platform
Add a new train
Enter a train number, name, route, seat count, and fare. The route must use known station names. If a station name is misspelled, the program suggests a close match. The train’s total distance is calculated from the shortest paths between consecutive stops.

Show all trains
Displays the trains currently loaded in the program, including their routes, distance, seats, and fare.

Search trains by keyword
Finds trains whose name or route contains your search word, such as “Chennai” or “Express.” It uses the KMP string-search algorithm.

Check a station name
Checks whether a typed station name is in the network. If it isn’t, the program can suggest the closest spelling using edit distance.

Find common stops for two trains
Given two train numbers, lists the stops they share in the same order. It uses the Longest Common Subsequence algorithm.

Find the shortest distance between stations
Choose two stations to see the shortest route and its distance. The program calculates shortest paths between all station pairs using Floyd–Warshall.

Load cargo into a wagon
Enter a wagon’s weight capacity. The program selects cargo items to maximize their total value without exceeding that capacity, using 0/1 knapsack.

Find maximum train capacity between stations
Choose a starting and ending station to find the maximum number of trains per day the network can carry between them, based on track capacities. It uses the Edmonds–Karp maximum-flow algorithm.

Assign crew to trains
Assigns crew members to trains, with each crew member assigned at most once. A crew member can drive a train if their home station is the train’s origin or destination. The program finds the largest possible set of assignments.

Plan an inspection tour of all stations
Finds a tour that starts at Hyderabad, visits every station, and returns to Hyderabad. It compares an exact solution with a faster approximate solution.

Place ticket checkers
Finds a set of stations where checkers can cover every track: each track must have a checker at one or both ends. It compares an exact solution with an approximation.

Find the weakest link in the network
Estimates the smallest set of tracks whose removal would split the network into separate parts. It uses repeated randomized trials, so the result is an estimate.

Sort trains by distance
Displays trains from shortest to longest route. It also compares two sorting approaches on already-sorted sample data.

Compare normal and parallel processing
Times sequential and parallel versions of sample calculations, including revenue totals and sorting. The speed difference depends on the computer.

Run all test cases
Runs the built-in checks for the algorithms and features and reports which checks pass or fail.

Exit
Closes the program. Train entries added during that run are not saved after it closes.