package planner.model;

import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public final class DistanceMatrix {
    private final List<Location> locations;
    private final double[][] distances;

    // Row zero is always the warehouse. Delivery indices follow input order.
    public DistanceMatrix(List<Location> points) {
        locations = List.copyOf(points);
        if (locations.size() < 2) throw new IllegalArgumentException("Supply at least one delivery.");
        var ids = new HashSet<String>();
        distances = new double[locations.size()][locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            Location a = locations.get(i);
            if (!ids.add(a.id())) throw new IllegalArgumentException("Duplicate ID: " + a.id());
            for (int j = 0; j < i; j++) {
                Location b = locations.get(j);
                double d = Math.hypot(a.x() - b.x(), a.y() - b.y());
                if (!Double.isFinite(d)) throw new IllegalArgumentException("Distance overflow.");
                distances[i][j] = distances[j][i] = d;
            }
        }
    }

    public List<Integer> inputOrder() {
        return IntStream.range(1, locations.size()).boxed().toList();
    }

    public String id(int index) { return locations.get(index).id(); }

    public double between(int a, int b) { return distances[a][b]; }

    public double cost(List<Integer> order) {
        if (!RouteValidator.isValid(order, locations.size() - 1)) {
            throw new IllegalArgumentException("Route must visit every delivery exactly once.");
        }
        int previous = 0;
        double total = 0;
        for (int next : order) {
            total += between(previous, next);
            previous = next;
        }
        total += between(previous, 0);
        if (!Double.isFinite(total)) throw new IllegalArgumentException("Tour distance overflow.");
        return total;
    }
}
