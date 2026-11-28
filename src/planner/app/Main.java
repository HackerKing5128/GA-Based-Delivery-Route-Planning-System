package planner.app;

import planner.ga.GeneticAlgorithm;
import planner.ga.Settings;
import planner.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class Main {
    private static final String USAGE =
            "Usage: planner.app.Main FILE|- [seed [population generations crossover mutation tournament elites]]";

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("--help")) {
            System.out.println(USAGE);
            return;
        }
        try {
            run(args);
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("Input error: " + e.getMessage());
            System.exit(2);
        }
    }

    private static void run(String[] args) throws IOException {
        if (args.length != 1 && args.length != 2 && args.length != 8) {
            throw new IllegalArgumentException(USAGE);
        }
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42L;
        Settings settings = Settings.DEFAULT;
        if (args.length == 8) {
            settings = new Settings(Integer.parseInt(args[2]), Integer.parseInt(args[3]),
                    Double.parseDouble(args[4]), Double.parseDouble(args[5]),
                    Integer.parseInt(args[6]), Integer.parseInt(args[7]));
        }
        DistanceMatrix matrix;
        try (var reader = args[0].equals("-")
                ? new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
                : Files.newBufferedReader(Path.of(args[0]))) {
            matrix = readMatrix(reader);
        }
        List<Integer> baseline = matrix.inputOrder();
        double initial = matrix.cost(baseline);
        long start = System.nanoTime();
        var result = GeneticAlgorithm.optimize(matrix, settings, seed);
        double millis = (System.nanoTime() - start) / 1_000_000.0;
        System.out.printf("Deliveries %d, seed %d, %s%n", baseline.size(), seed, settings);
        printRoute("Input order", matrix, baseline, initial);
        printRoute("GA best", matrix, result.order(), result.distance());
        if (initial == 0) {
            System.out.println("Improvement N/A (zero baseline)");
        } else {
            System.out.printf(Locale.ROOT, "Improvement %.2f%%%n", (initial - result.distance()) / initial * 100);
        }
        System.out.printf(Locale.ROOT, "GA time %.3f ms%n", millis);
        System.out.println("GA route valid: " + RouteValidator.isValid(result.order(), baseline.size()));
        System.out.println("Convergence (best-so-far distance at generation 0 and each decrease):");
        List<Double> history = result.bestByGeneration();
        for (int i = 0; i < history.size(); i++) {
            if (i == 0 || history.get(i) < history.get(i - 1)) {
                System.out.printf(Locale.ROOT, "  %d  %.6f%n", i, history.get(i));
            }
        }
        System.out.println("Final generation evaluated: " + (history.size() - 1));
    }

    private static DistanceMatrix readMatrix(BufferedReader reader) throws IOException {
        var points = new ArrayList<Location>();
        String line;
        int number = 0;
        while ((line = reader.readLine()) != null) {
            number++;
            if (line.isBlank()) continue;
            String[] fields = line.strip().split("\\s+");
            if (fields.length != 3) throw new IllegalArgumentException("Expected ID X Y on line " + number);
            try {
                points.add(new Location(fields[0], Double.parseDouble(fields[1]), Double.parseDouble(fields[2])));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Line " + number + ": " + e.getMessage());
            }
        }
        return new DistanceMatrix(points);
    }

    private static void printRoute(String name, DistanceMatrix matrix, List<Integer> order, double cost) {
        var labels = new StringJoiner(" -> ");
        labels.add(matrix.id(0));
        for (int index : order) labels.add(matrix.id(index));
        labels.add(matrix.id(0));
        System.out.printf(Locale.ROOT, "%s: %s%nDistance %.6f units%n", name, labels, cost);
    }
}
