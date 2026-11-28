package planner.ga;

import planner.model.DistanceMatrix;
import java.util.*;

public final class GeneticAlgorithm {
    private GeneticAlgorithm() {}

    private record Candidate(List<Integer> order, double cost, double fitness) {
        static Candidate evaluate(List<Integer> order, DistanceMatrix matrix) {
            double cost = matrix.cost(order);
            double fitness = cost == 0 ? Double.MAX_VALUE : Math.min(Double.MAX_VALUE, 1.0 / cost);
            return new Candidate(List.copyOf(order), cost, fitness);
        }
    }

    private static final Comparator<Candidate> BEST_FIRST =
            Comparator.comparingDouble(Candidate::fitness).reversed().thenComparingDouble(Candidate::cost);

    public record Result(List<Integer> order, double distance, List<Double> bestByGeneration) {}

    public static Result optimize(DistanceMatrix matrix, Settings settings, long seed) {
        Random rng = new Random(seed);
        List<Integer> input = matrix.inputOrder();
        var population = new ArrayList<Candidate>(settings.population());
        for (int i = 0; i < settings.population(); i++) {
            population.add(Candidate.evaluate(Operators.shuffled(input, rng), matrix));
        }
        var history = new ArrayList<Double>();
        Candidate best = Collections.min(population, BEST_FIRST);
        history.add(best.cost());
        for (int generation = 0; generation < settings.generations(); generation++) {
            population.sort(BEST_FIRST);
            var next = new ArrayList<Candidate>(population.subList(0, settings.elites()));
            while (next.size() < settings.population()) {
                var first = tournament(population, settings.tournament(), rng).order();
                List<Integer> child;
                if (rng.nextDouble() < settings.crossover()) {
                    var second = tournament(population, settings.tournament(), rng).order();
                    child = Operators.crossover(first, second, rng);
                } else {
                    child = new ArrayList<>(first);
                }
                if (rng.nextDouble() < settings.mutation()) Operators.mutate(child, rng);
                next.add(Candidate.evaluate(child, matrix));
            }
            population = next;
            Candidate winner = Collections.min(population, BEST_FIRST);
            if (BEST_FIRST.compare(winner, best) < 0) best = winner;
            history.add(best.cost());
        }
        return new Result(best.order(), best.cost(), List.copyOf(history));
    }

    private static Candidate tournament(List<Candidate> population, int size, Random rng) {
        Candidate winner = population.get(rng.nextInt(population.size()));
        for (int i = 1; i < size; i++) {
            Candidate sample = population.get(rng.nextInt(population.size()));
            if (BEST_FIRST.compare(sample, winner) < 0) winner = sample;
        }
        return winner;
    }
}
