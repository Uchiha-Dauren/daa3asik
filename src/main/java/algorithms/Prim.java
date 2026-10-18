package algorithms;

import java.util.PriorityQueue;

import graph.Graph;
import metrics.Metrics;


public final class Prim {
    private Prim() {}

    public static MstResult run(Graph g, Metrics mt) {
        int n = g.n();
        boolean[] inTree = new boolean[n];
        PriorityQueue<long[]> pq = new PriorityQueue<>((x, y) -> {
            mt.comparisons++;
            return Long.compare(x[0], y[0]);
        });
        long total = 0;
        int taken = 0, comps = 0;
        for (int s = 0; s < n; s++) {
            if (inTree[s]) continue;
            comps++;
            pq.add(new long[]{0, s});
            while (!pq.isEmpty()) {
                mt.depth(pq.size());
                long[] top = pq.poll();
                int v = (int) top[1];
                if (inTree[v]) continue;         
                inTree[v] = true;
                if (v != s) { total += top[0]; taken++; }
                for (int i = 0; i < g.degree(v); i++) {
                    mt.edgeScans++;
                    int u = g.to(v, i);
                    if (!inTree[u]) pq.add(new long[]{g.weight(v, i), u});
                }
            }
        }
        return new MstResult(total, taken, comps);
    }
}
