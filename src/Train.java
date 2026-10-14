/** One train: number, name, route (list of station indexes), seats and fare. */
public class Train {
    int number;
    String name;
    int[] route;
    int seats;
    int fare;
    int distance; // total km along the route (uses shortest paths from Floyd-Warshall)

    Train(int number, String name, int[] route, int seats, int fare) {
        this.number = number;
        this.name = name;
        this.route = route;
        this.seats = seats;
        this.fare = fare;
        for (int i = 0; i + 1 < route.length; i++) {
            this.distance += DPAlgos.DIST[route[i]][route[i + 1]];
        }
    }

    int source() { return route[0]; }

    int destination() { return route[route.length - 1]; }

    String[] routeNames() {
        String[] names = new String[route.length];
        for (int i = 0; i < route.length; i++) names[i] = Data.STATIONS[route[i]];
        return names;
    }

    String routeText() { return String.join(" > ", routeNames()); }
}
