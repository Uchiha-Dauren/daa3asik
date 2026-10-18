package algorithms;

import graph.Graph;
import metrics.Metrics;


public final class Kahn {
    private Kahn() {}

    public static TopoResult run(Graph g, Metrics mt) {
        int n = g.n();
        int[] indeg = new int[n];
        for (int i = 0; i < g.m(); i++) indeg[g.edgeV(i)]++;  
        int[] q = new int[n];
        int head = 0, tail = 0;
        for (int v = 0; v < n; v++) if (indeg[v] == 0) q[tail++] = v;
        mt.depth(tail - head);
        while (head < tail) {
            int v = q[head++];
            for (int i = 0; i < g.degree(v); i++) {
                mt.edgeScans++;
                int u = g.to(v, i);
                if (--indeg[u] == 0) { q[tail++] = u; mt.depth(tail - head); }
            }
        }
        return new TopoResult(q, tail, tail < n);
    }

    public static boolean isValidOrder(Graph g, TopoResult r) {
        if (r.hasCycle() || r.size() != g.n()) return false;
        int[] pos = new int[g.n()];
        for (int i = 0; i < r.size(); i++) pos[r.order()[i]] = i;
        for (int i = 0; i < g.m(); i++) if (pos[g.edgeU(i)] >= pos[g.edgeV(i)]) return false;
        return true;
    }
}
