package planner.model;

import java.util.List;

public final class RouteValidator {
    private RouteValidator() {}

    public static boolean isValid(List<Integer> order, int deliveries) {
        if (order == null || order.size() != deliveries) return false;
        boolean[] seen = new boolean[deliveries + 1];
        for (Integer index : order) {
            if (index == null || index < 1 || index > deliveries || seen[index]) return false;
            seen[index] = true;
        }
        return true;
    }
}
