# GA-Based-Delivery-Route-Planning-System
GLA University major project in Soft Computing. The first milestone is a Java console program that plans a round trip for one delivery vehicle. It compares the original delivery order with the shortest route found by a genetic algorithm.

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
