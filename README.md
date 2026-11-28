# Genetic Algorithm-Based Delivery Route Planning System

GLA University · B.Tech. Computer Science & Engineering · Group G325 · 2026–27

Given a warehouse and a set of deliveries, which visiting order keeps the travel cost low? This Soft Computing project uses a Genetic Algorithm (GA) implemented in Java to search for efficient routes. The broader project also considers how to assign deliveries when several vehicles have different carrying capacities. [Project synopsis (PDF)](docs/G325-Synopsis.pdf)

## How route planning works

The planner starts with predefined delivery locations and a matrix of travel costs between them. In the single-vehicle prototype, each candidate route is an ordering of the deliveries. The engine scores the complete round trip, selects promising parents, and creates new orders through crossover and mutation. Elitism carries the strongest candidates into the next generation. The planner returns the best route it encountered during the run.

The original input order gives the team a simple baseline. Both routes use the same warehouse, deliveries, and cost matrix, so their costs can be compared directly.

## Development milestones

| Stage | Expected outcome | Status |
| --- | --- | --- |
| Single-vehicle engine | Visit each delivery once and return to the warehouse. Minimize 2D Euclidean distance and report the route, improvement, execution time, validity, and convergence. | Implemented and tested on the milestone branch |
| Vehicle assignment and capacity | Use package weights and vehicle capacities to assign deliveries, check whether each vehicle can carry its load, and evaluate the resulting routes. | Broader target in the synopsis, with detailed scope to confirm |
| Application interface | Let users configure delivery inputs and GA settings, inspect routes, and visualize how cost and fitness change over generations. | Planned. Spring Boot is a candidate for integration. Interface and storage choices remain open. |
| Evaluation | Compare the GA with the baseline on predefined scenarios and repeat runs with different seeds. Report route cost, time, variation across runs, and capacity feasibility when vehicle assignment is available. | Initial single-vehicle experiments are available. Broader evaluation is planned. |

The synopsis also proposes a stretch experiment: change selected travel costs during a simulated disruption, then replan the unfinished deliveries and compare the outcome with following the original route.

## Run the first milestone

The console demo and tests are on [the Java engine branch](https://github.com/HackerKing5128/GA-Based-Delivery-Route-Planning-System/tree/milestone/java-ga-engine). Its [build and demo instructions](https://github.com/HackerKing5128/GA-Based-Delivery-Route-Planning-System/blob/milestone/java-ga-engine/README.md) cover compiling with a JDK, running the tests, and trying a ten-delivery scenario. The [engine walkthrough](https://github.com/HackerKing5128/GA-Based-Delivery-Route-Planning-System/blob/milestone/java-ga-engine/docs/ga-engine.md) explains the route representation, genetic operators, and measured results.
