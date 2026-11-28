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
    }
}
