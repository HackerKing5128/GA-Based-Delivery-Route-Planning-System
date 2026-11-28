# Genetic Algorithm-Based Delivery Route Planning System

GLA University · B.Tech. Computer Science & Engineering · Group G325 · 2026–27

Given a warehouse and a set of deliveries, which visiting order keeps the travel cost low? This Soft Computing project uses a Genetic Algorithm (GA) implemented in Java to search for efficient routes. The broader project also considers how to assign deliveries when several vehicles have different carrying capacities. [Project synopsis (PDF)](docs/G325-Synopsis.pdf)

## How route planning works

The planner starts with predefined delivery locations and a matrix of travel costs between them. In the single-vehicle prototype, each candidate route is an ordering of the deliveries. The engine scores the complete round trip, selects promising parents, and creates new orders through crossover and mutation. Elitism carries the strongest candidates into the next generation. The planner returns the best route it encountered during the run.

The original input order gives the team a simple baseline. Both routes use the same warehouse, deliveries, and cost matrix, so their costs can be compared directly.

## Development milestones

| Stage | Expected outcome | Status |
| --- | --- | --- |
| Single-vehicle engine | Visit each delivery once and return to the warehouse. Minimize 2D Euclidean distance and report the route, improvement, execution time, validity, and convergence. | Implemented and tested |
| Vehicle assignment and capacity | Use package weights and vehicle capacities to assign deliveries, check whether each vehicle can carry its load, and evaluate the resulting routes. | Broader target in the synopsis, with detailed scope to confirm |
| Application interface | Let users configure delivery inputs and GA settings, inspect routes, and visualize how cost and fitness change over generations. | Planned. Spring Boot is a candidate for integration. Interface and storage choices remain open. |
| Evaluation | Compare the GA with the baseline on predefined scenarios and repeat runs with different seeds. Report route cost, time, variation across runs, and capacity feasibility when vehicle assignment is available. | Initial single-vehicle experiments are available. Broader evaluation is planned. |

The synopsis also proposes a stretch experiment: change selected travel costs during a simulated disruption, then replan the unfinished deliveries and compare the outcome with following the original route.

## Run the milestone demo

Use PowerShell from the repository root and a JDK that supports Java 17. Adjust the first line to your JDK installation. These commands were tested with JDK 22.0.1.

```powershell
$jdk = 'C:\Program Files\Java\jdk-22\bin'
$sources = (Get-ChildItem src,test -Recurse -Filter *.java).FullName
& "$jdk\javac" --release 17 -d out $sources
& "$jdk\java" -cp out planner.ga.EngineTest
Get-Content examples/deliveries.txt -TotalCount 11 | & "$jdk\java" -cp out planner.app.Main - 42
```

The last command reads the warehouse and ten deliveries. With seed 42 the input-order distance is 156.125911 units and the best route costs 67.898882 units. The console prints both routes, their costs, percentage improvement, GA execution time, validity, and the generations when the best distance decreased.

## Try your own locations or settings

Use a UTF-8 file with one space-separated `ID X Y` per line. Put the warehouse first, followed by at least one delivery. IDs must be unique and coordinates finite. Blank lines are skipped. Distances are in coordinate units.

```powershell
& "$jdk\java" -cp out planner.app.Main examples/deliveries.txt 42 100 500 0.90 0.05 3 2
```

After the file path, the arguments are seed, population size, generations, crossover probability, mutation probability, tournament size, and elite count. The values shown are the defaults. Supply just the file, the file and seed, or the full set. Use `-` in place of the path to read standard input.

## Project layout

The model, distance matrix and validator live in `src/planner/model`. The optimizer and its settings live in `src/planner/ga`. The console calls the optimizer through `GeneticAlgorithm.optimize(matrix, settings, seed)`, the same entry point a future application can use.

For a worked route, a crossover example, and the measured milestone results, see [How the GA engine works](docs/ga-engine.md).
