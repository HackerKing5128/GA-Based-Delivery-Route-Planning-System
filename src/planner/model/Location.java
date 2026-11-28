package planner.model;

public record Location(String id, double x, double y) {
    public Location {
        if (id == null || id.isBlank() || !Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("Each location needs an ID and finite coordinates.");
        }
    }
}
