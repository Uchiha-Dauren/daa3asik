## Counter / formula ratios

| Algorithm | ratio | density | 1000 | 3162 | 10000 | 31623 | 100000 |
|---|---|---|---|---|---|---|---|
| Kruskal | dsu_ops / E | sparse | 1.791 | 2.019 | 2.182 | 2.232 | 2.248 |
| Kruskal | dsu_ops / E | dense | 0.480 | 0.506 | 0.465 | 0.654 | 0.794 |
| Kruskal | comparisons / (E log2 E) | sparse | 0.894 | 0.909 | 0.919 | 0.926 | 0.933 |
| Kruskal | comparisons / (E log2 E) | dense | 0.913 | 0.921 | 0.929 | 0.935 | 0.940 |
| Prim | comparisons / (E log2 V) | sparse | 2.196 | 2.186 | 2.180 | 2.178 | 2.167 |
| Prim | comparisons / (E log2 V) | dense | 2.616 | 2.533 | 2.473 | 2.421 | 2.381 |
| Kosaraju | edgeScans / (V+E) | sparse | 1.600 | 1.600 | 1.600 | 1.600 | 1.600 |
| Kosaraju | edgeScans / (V+E) | dense | 1.905 | 1.905 | 1.905 | 1.905 | 1.905 |
| Kahn | edgeScans / (V+E) | sparse | 0.800 | 0.800 | 0.800 | 0.800 | 0.800 |
| Kahn | edgeScans / (V+E) | dense | 0.952 | 0.952 | 0.952 | 0.952 | 0.952 |
| DagShortestPath | edgeScans / (V+E) | sparse | 0.800 | 0.800 | 0.800 | 0.800 | 0.800 |
| DagShortestPath | edgeScans / (V+E) | dense | 0.952 | 0.952 | 0.952 | 0.952 | 0.952 |

## Log-log slopes of edgeScans (and other counters) vs n

| Algorithm | density | slope edgeScans | slope total ops |
|---|---|---|---|
| Kruskal | sparse | 1.055 | 1.095 |
| Kruskal | dense | 1.119 | 1.090 |
| Prim | sparse | 1.000 | 1.101 |
| Prim | dense | 1.000 | 1.085 |
| Kosaraju | sparse | 1.000 | 1.000 |
| Kosaraju | dense | 1.000 | 1.000 |
| Kahn | sparse | 1.000 | 1.000 |
| Kahn | dense | 1.000 | 1.000 |
| DagShortestPath | sparse | 1.000 | 1.000 |
| DagShortestPath | dense | 1.000 | 1.000 |

## Kruskal: share of edges examined (edgeScans / m)

- sparse: 0.770, 0.885, 0.966, 0.991, 0.999
- dense: 0.215, 0.228, 0.207, 0.302, 0.372

## Scans per edge (edgeScans / m)

- Kruskal sparse: 0.770, 0.885, 0.966, 0.991, 0.999
- Kruskal dense: 0.215, 0.228, 0.207, 0.302, 0.372
- Prim sparse: 2.000, 2.000, 2.000, 2.000, 2.000
- Prim dense: 2.000, 2.000, 2.000, 2.000, 2.000
- Kosaraju sparse: 2.000, 2.000, 2.000, 2.000, 2.000
- Kosaraju dense: 2.000, 2.000, 2.000, 2.000, 2.000
- Kahn sparse: 1.000, 1.000, 1.000, 1.000, 1.000
- Kahn dense: 1.000, 1.000, 1.000, 1.000, 1.000
- DagShortestPath sparse: 1.000, 1.000, 1.000, 1.000, 1.000
- DagShortestPath dense: 1.000, 1.000, 1.000, 1.000, 1.000

## Median times, ms (not evidence, only context)

| Algorithm | density | 1000 | 3162 | 10000 | 31623 | 100000 |
|---|---|---|---|---|---|---|
| Kruskal | sparse | 1.4 | 3.5 | 9.1 | 16.4 | 56.2 |
| Kruskal | dense | 3.9 | 15.3 | 24.3 | 80.5 | 289.8 |
| Prim | sparse | 7.1 | 6.8 | 9.1 | 37.0 | 158.8 |
| Prim | dense | 13.4 | 32.2 | 63.1 | 279.8 | 1274.3 |
| Kosaraju | sparse | 2.0 | 1.1 | 4.2 | 9.9 | 43.3 |
| Kosaraju | dense | 1.7 | 7.5 | 12.3 | 67.6 | 203.9 |
| Kahn | sparse | 0.6 | 0.2 | 0.4 | 1.4 | 5.6 |
| Kahn | dense | 0.2 | 0.2 | 0.8 | 3.6 | 18.4 |
| DagShortestPath | sparse | 0.8 | 0.5 | 1.0 | 3.3 | 12.6 |
| DagShortestPath | dense | 2.7 | 0.7 | 2.1 | 7.3 | 34.9 |
