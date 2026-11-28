package planner.ga;

public record Settings(int population, int generations, double crossover, double mutation,
                       int tournament, int elites) {
    public static final Settings DEFAULT = new Settings(100, 500, 0.90, 0.05, 3, 2);

    public Settings {
        if (population < 2 || generations < 0 || tournament < 1 || tournament > population
                || elites < 0 || elites >= population) {
            throw new IllegalArgumentException("Check population, generations, tournament size and elite count.");
        }
        if (!(crossover >= 0 && crossover <= 1 && mutation >= 0 && mutation <= 1)) {
            throw new IllegalArgumentException("Probabilities must be between 0 and 1.");
        }
    }
}
