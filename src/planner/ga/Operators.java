package planner.ga;

import java.util.*;

final class Operators {
    private Operators() {}

    static List<Integer> shuffled(List<Integer> order, Random rng) {
        var copy = new ArrayList<>(order);
        Collections.shuffle(copy, rng);
        return copy;
    }

    static List<Integer> crossover(List<Integer> first, List<Integer> second, Random rng) {
        int size = first.size();
        if (size == 1) return new ArrayList<>(first);
        int a = rng.nextInt(size);
        int b = otherIndex(a, size, rng);
        return orderCrossover(first, second, Math.min(a, b), Math.max(a, b));
    }

    static List<Integer> orderCrossover(List<Integer> first, List<Integer> second, int low, int high) {
        int size = first.size();
        var child = new ArrayList<Integer>(Collections.nCopies(size, 0));
        boolean[] used = new boolean[size + 1];
        for (int i = low; i <= high; i++) {
            int gene = first.get(i);
            child.set(i, gene);
            used[gene] = true;
        }
        int write = (high + 1) % size;
        for (int offset = 0; offset < size; offset++) {
            int gene = second.get((high + 1 + offset) % size);
            if (!used[gene]) {
                child.set(write, gene);
                write = (write + 1) % size;
            }
        }
        return child;
    }

    // Only called on an offspring's own mutable list.
    static void mutate(List<Integer> child, Random rng) {
        if (child.size() < 2) return;
        int a = rng.nextInt(child.size());
        Collections.swap(child, a, otherIndex(a, child.size(), rng));
    }

    private static int otherIndex(int a, int size, Random rng) {
        int b = rng.nextInt(size - 1);
        return b >= a ? b + 1 : b;
    }
}
