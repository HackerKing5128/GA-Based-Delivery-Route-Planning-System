package planner.ga;

import planner.model.*;
import java.util.*;

public final class EngineTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    private static void rejects(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected input rejection");
    }

    public static void main(String[] args) {
        var triangle = new DistanceMatrix(List.of(new Location("W", 0, 0),
                new Location("A91", 3, 0), new Location("B04", 3, 4)));
        check(triangle.between(0, 2) == 5 && triangle.between(2, 0) == 5
                && triangle.between(1, 1) == 0, "3-4-5 matrix");
        check(triangle.cost(triangle.inputOrder()) == 12, "Return leg");
        for (var invalid : List.of(List.of(1), List.of(1, 1), List.of(0, 2), List.of(1, 3))) {
            rejects(() -> triangle.cost(invalid));
        }
        var depot = new Location("W", 1, 1);
        rejects(() -> new DistanceMatrix(List.of(depot)));
        rejects(() -> new DistanceMatrix(List.of(depot, depot)));
        rejects(() -> new Location("X", Double.NaN, 0));
        rejects(() -> new Settings(5, -1, 1, 1, 2, 0));
        rejects(() -> new Settings(5, 0, Double.NaN, 1, 2, 0));
        rejects(() -> new Settings(5, 0, 0, 1, 6, 0));
        rejects(() -> new Settings(5, 0, 0, 1, 2, 5));
        var same = new DistanceMatrix(List.of(depot, new Location("Other", 1, 1)));
        var zero = GeneticAlgorithm.optimize(same, new Settings(3, 3, 1, 1, 1, 0), 7);
        check(zero.distance() == 0 && zero.order().equals(List.of(1)), "Coincident delivery");
        var first = List.of(1, 2, 3, 4, 5, 6);
        var second = List.of(4, 6, 5, 2, 1, 3);
        check(Operators.orderCrossover(first, second, 1, 3).equals(List.of(5, 2, 3, 4, 1, 6)), "OX wrap");
        var rng = new Random(123);
        boolean finalImprovement = false;
        for (int n : List.of(5, 10, 20)) {
            var points = new ArrayList<Location>();
            points.add(depot);
            for (int i = 1; i <= n; i++) {
                points.add(new Location("D" + i, rng.nextInt(100), rng.nextInt(100)));
            }
            var matrix = new DistanceMatrix(points);
            for (int seed = 0; seed < 30; seed++) {
                var parent = List.copyOf(Operators.shuffled(matrix.inputOrder(), rng));
                check(RouteValidator.isValid(parent, n), "Initial permutation");
                var child = Operators.crossover(parent, matrix.inputOrder(), rng);
                check(RouteValidator.isValid(child, n), "Crossover permutation");
                var beforeSwap = List.copyOf(child);
                Operators.mutate(child, rng);
                int changed = 0;
                for (int i = 0; i < n; i++) if (!beforeSwap.get(i).equals(child.get(i))) changed++;
                check(changed == 2 && RouteValidator.isValid(child, n), "Swap permutation");

                var settings = new Settings(9, 5, 1, 1, 3, seed % 2);
                var result = GeneticAlgorithm.optimize(matrix, settings, seed);
                check(result.equals(GeneticAlgorithm.optimize(matrix, settings, seed)), "Repeatable seed");
                var history = result.bestByGeneration();
                check(history.size() == 6 && result.distance() == matrix.cost(result.order())
                        && history.get(5) == result.distance(), "Best evaluated tour");
                for (int i = 1; i < history.size(); i++) {
                    check(history.get(i) <= history.get(i - 1), "Best retained");
                }
                finalImprovement |= history.get(5) < history.get(4);
            }
        }
        check(finalImprovement, "Covered an improving final generation");
        var initialOnly = GeneticAlgorithm.optimize(triangle, new Settings(3, 0, 0, 0, 1, 0), 5);
        check(initialOnly.bestByGeneration().equals(List.of(12.0)), "Generation zero");
        System.out.println("Engine checks passed (geometry, validation, operators, seeded evolution).");
    }
}
