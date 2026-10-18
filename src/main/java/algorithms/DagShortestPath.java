package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import graph.Graph;
import metrics.Metrics;


public final class DagShortestPath {
    public static final long INF = Long.MAX_VALUE / 4;
    public final long[] dist;
    public final int[] parent;   // -1 for sources

    private DagShortestPath(long[] dist, int[] parent) { this.dist = dist; this.parent = parent; }

    public static DagShortestPath run(Graph g, Metrics mt) {
        int n = g.n();
        TopoResult t = Kahn.run(g, new Metrics());          // order itself is not counted here
        if (t.hasCycle()) throw new IllegalArgumentException("graph has a cycle, not a DAG");
        boolean[] hasIn = new boolean[n];
        for (int i = 0; i < g.m(); i++) hasIn[g.edgeV(i)] = true;
        long[] dist = new long[n];
        int[] parent = new int[n];
        for (int v = 0; v < n; v++) { dist[v] = hasIn[v] ? INF : 0; parent[v] = -1; }
        for (int k = 0; k < n; k++) {
            int v = t.order()[k];
            if (dist[v] == INF) continue;
            for (int i = 0; i < g.degree(v); i++) {
                mt.edgeScans++;
                int u = g.to(v, i);
                long nd = dist[v] + g.weight(v, i);
                mt.comparisons++;
                if (nd < dist[u]) { dist[u] = nd; parent[u] = v; }
            }
        }
        return new DagShortestPath(dist, parent);
    }

    public long sumOfDistances() {
        long s = 0;
        for (long d : dist) if (d != INF) s += d;
        return s;
    }

    /** Path from some source to v, restored through parent[]. */
    public List<Integer> pathTo(int v) {
        List<Integer> p = new ArrayList<>();
        for (int x = v; x != -1; x = parent[x]) p.add(x);
        Collections.reverse(p);
        return p;
    }
}
