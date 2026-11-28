# How the GA engine works

Given a warehouse and a list of deliveries, which visiting order gives the driver a short round trip? With ten deliveries there are 3,628,800 possible orders. The engine explores a sample of them. It scores complete routes, breeds new orders from promising ones, and keeps track of the shortest tour it has seen.

## From coordinates to one scored route

Consider this input.

```text
Warehouse 0 0
A 3 0
B 3 4
```

The first row is always the warehouse, at index 0. A and B get indices 1 and 2. A chromosome is a list of delivery indices, so `[1, 2]` means `Warehouse → A → B → Warehouse`. Keeping index 0 outside the chromosome fixes the start and end even when deliveries change positions. The validator requires each index from 1 through the delivery count exactly once. [Route validator](../src/planner/model/RouteValidator.java)

The engine calculates straight-line distances once, stores them in a symmetric matrix, and looks them up for each route. Here the three legs cost 3, 4 and 5 units. The return leg costs 5 because the horizontal and vertical differences are 3 and 4. The complete tour costs **12 units**, giving it a fitness of **1/12**. A shorter positive-distance tour earns a larger fitness. A tour of coincident points costs zero and receives the largest finite fitness value. [Distance matrix and cost calculation](../src/planner/model/DistanceMatrix.java)

## How one generation produces the next

1. **Initialize.** Independently shuffle the input order once per candidate. The default population has 100 candidates. Repeated orders are possible. The engine evaluates this starting population as generation zero. The original input order provides a separate comparison baseline.
2. **Keep the elites.** At the start of each generation, copy the two highest-ranked candidates into the next population by default. Their orders are immutable, so breeding a child cannot alter either elite.
3. **Select a parent.** Draw three candidates at random with replacement and keep the one with the highest fitness. A candidate may enter a tournament more than once. When crossover is chosen, run another tournament to select the second parent.
4. **Build a child.** Each child has a 90% chance of order crossover. Otherwise it starts as a copy of the first parent. Independently, each child has a 5% chance of swapping two distinct delivery positions. A one-delivery route has only one possible order.
5. **Evaluate and repeat.** Score each child and fill the population back to its configured size. Record the best distance seen so far, then proceed to the next generation. With the default limit, the history contains 501 values, covering generations 0 through 500. Even with the elite count set to zero, the result retains the best route encountered. [Evolution loop](../src/planner/ga/GeneticAlgorithm.java) · [Configuration](../src/planner/ga/Settings.java)

### Order crossover by hand

Suppose the first parent is `A B C D E F` and the second is `D F E B A C`. Copy positions 2 through 4 from the first parent. The partial child is `_ B C D _ _`.

Read the second parent starting just after position 4 and wrap around once. The reading order is `A C D F E B`. Skip B, C and D because they already occupy the copied segment. This leaves `A F E`. Fill the child's empty positions starting just after the segment. A goes into position 5, F into position 6, and E wraps around to position 1.

The child is **`E B C D A F`**. If mutation then swaps positions 1 and 5, the child becomes **`A B C D E F`**. Every delivery still occurs exactly once. The test suite checks this crossover example using indices 1 through 6. [Genetic operators](../src/planner/ga/Operators.java) · [Engine tests](../test/planner/ga/EngineTest.java)

## Reading the console results

The README demo takes the first ten deliveries from the example file. The original input order costs **156.125911 units**. The best of the initial 100 shuffles costs **83.294449**. With seed 42, evolution then produces these improvements.

| Generation | Best distance so far |
| ---: | ---: |
| 0 | 83.294449 |
| 1 | 76.533931 |
| 5 | 69.535129 |
| 7 | 68.542192 |
| 22 | 67.898882 |

The console prints a row when the best distance decreases. The best route found at generation 22 held through generation 500. It visits every delivery once and returns to the warehouse.

```text
Warehouse → P09 → Shop → North → P07 → P10 → DepotRoad → P08 → A04 → C17 → P06 → Warehouse
```

The displayed improvement uses the input order as its reference: `100 × (156.125911 − 67.898882) / 156.125911 = 56.51%` after rounding. The change from 83.294449 to 67.898882 shows what evolution added after initialization. A run can produce a negative improvement. If the baseline is zero, the percentage is undefined and the console prints N/A. The timer covers optimization from initialization through result construction. Input, matrix setup and printing occur outside it. [Console reporting](../src/planner/app/Main.java)

### Results across delivery counts

Each run used the warehouse and the first N deliveries from the same file, seed 42, and the default GA settings. All returned routes passed validation.

| Deliveries | Input order | Best found | Improvement | GA time in this run |
| ---: | ---: | ---: | ---: | ---: |
| 5 | 76.782400 | 50.574228 | 34.13% | 52.844 ms |
| 10 | 156.125911 | 67.898882 | 56.51% | 61.163 ms |
| 20 | 303.609417 | 92.171850 | 69.64% | 69.939 ms |

To repeat these runs, use the README command with `-TotalCount` set to 6, 11, or 21 respectively. The extra row is the warehouse. A fixed seed reproduces the search for the same input and settings. Timing varies between runs. These distances describe synthetic coordinates, and the best route found is a heuristic result. [Example data](../examples/deliveries.txt) · [Build and demo commands](../README.md)
