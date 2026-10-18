# DAA Assignment 3 — Graphs (DSU, MST, SCC, Kahn, DAG shortest paths)

Java 17, Maven, JUnit 5. All algorithms are written from scratch (primitive arrays everywhere; `java.util.PriorityQueue` is used only as the heap in Prim).

## Layout
```
src/main/java/graph        Graph (one class, directed/undirected, adjacency list), GraphGenerator (seed 42)
src/main/java/algorithms   DSU, Kruskal, Prim, Kosaraju, Kahn, DagShortestPath
src/main/java/metrics      Metrics (counters)
src/main/java/benchmark    Benchmark (self-checking, writes results/results.csv)
src/test/java              JUnit 5 tests
data/                      disconnected.txt, cyclic.txt, dag.txt  (format: "n m directed|undirected", then "u v w")
results/                   results.csv, analysis.md, plots/*.png
scripts/analyze.py         ratios, slopes and the three plots (needs python3 + matplotlib)
REPORT.md                  report
```

## Build, test, run
```bash
mvn test                              # 12 JUnit tests (DSU, MST, SCC vs brute force, Kahn, DAG vs Bellman-Ford, 100 000-vertex stack test)
mvn -q compile exec:java              # benchmark: writes results/results.csv, FAILS LOUDLY if a reference value is wrong
python3 scripts/analyze.py            # plots + results/analysis.md
```
Without Maven: `javac -d out $(find src/main -name '*.java') && java -Xmx3g -cp out benchmark.Benchmark`

## Git workflow (do this on your own machine)
```bash
git init && git checkout -b main
# per feature: git checkout -b feature/dsu ... commit small steps ... merge into main
# examples: feat(dsu): add path compression | test(mst): compare kruskal and prim weights | docs(report): add plots
git tag v1.0 && git push origin main --tags
```
Branches: `feature/dsu`, `feature/mst`, `feature/scc`, `feature/paths`, `feature/metrics`.

## AI use acknowledgement
_Write your own honest statement here._ Example: "I used Claude (Anthropic) to generate the initial project structure, code, tests and report draft. I read and ran everything, and I can explain every algorithm and counter in the defense."
# daa3asik
