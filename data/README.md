# RailSync sample dataset

These CSV files export the fixed sample records currently hardcoded in `src/Data.java` and `src/Railsync.java`. Station IDs are zero-based and match the station array order in the Java code. A track is an undirected connection; its distance and capacity are approximate demonstration values. Train routes preserve their stop order; route IDs and names use semicolons so the route remains one CSV field. Cargo values are in thousands of rupees, as described in `Data.java`.

The application currently loads data from Java constants and does not read these CSV files. Menu-entered trains are held in memory and are not part of this sample export.
1. Add a new train
   Enter a train number, name, route, seat count, and fare. The route must use known station names. If a station name is misspelled, the program suggests a close match. The train’s total distance is calculated from the shortest paths between consecutive stops.
2. Show all trains
   Displays the trains currently loaded in the program, including their routes, distance, seats, and fare.
3. Search trains by keyword
   Finds trains whose name or route contains your search word, such as “Chennai” or “Express.” It uses the KMP string-search algorithm.
4. Check a station name
   Checks whether a typed station name is in the network. If it isn’t, the program can suggest the closest spelling using edit distance.
5. Find common stops for two trains
   Given two train numbers, lists the stops they share in the same order. It uses the Longest Common Subsequence algorithm.
6. Find the shortest distance between stations
   Choose two stations to see the shortest route and its distance. The program calculates shortest paths between all station pairs using Floyd–Warshall.
7. Load cargo into a wagon
   Enter a wagon’s weight capacity. The program selects cargo items to maximize their total value without exceeding that capacity, using 0/1 knapsack.
8. Find maximum train capacity between stations
   Choose a starting and ending station to find the maximum number of trains per day the network can carry between them, based on track capacities. It uses the Edmonds–Karp maximum-flow algorithm.
9. Assign crew to trains
   Assigns crew members to trains, with each crew member assigned at most once. A crew member can drive a train if their home station is the train’s origin or destination. The program finds the largest possible set of assignments.
10. Plan an inspection tour of all stations
    Finds a tour that starts at Hyderabad, visits every station, and returns to Hyderabad. It compares an exact solution with a faster approximate solution.
11. Place ticket checkers
    Finds a set of stations where checkers can cover every track: each track must have a checker at one or both ends. It compares an exact solution with an approximation.
12. Find the weakest link in the network
    Estimates the smallest set of tracks whose removal would split the network into separate parts. It uses repeated randomized trials, so the result is an estimate.
13. Sort trains by distance
    Displays trains from shortest to longest route. It also compares two sorting approaches on already-sorted sample data.
14. Compare normal and parallel processing
    Times sequential and parallel versions of sample calculations, including revenue totals and sorting. The speed difference depends on the computer.
15. Run all test cases
    Runs the built-in checks for the algorithms and features and reports which checks pass or fail.
16. Exit
    Closes the program. Train entries added during that run are not saved after it closes.


2:07 PM