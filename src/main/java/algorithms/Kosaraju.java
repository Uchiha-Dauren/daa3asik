package algorithms;

import graph.Graph;
import metrics.Metrics;

public final class Kosaraju {
    private Kosaraju() {}

    public static SccResult run(Graph g, Metrics mt) {
        int n = g.n();
        // pass 1: finish order on g
        int[] order = new int[n];
        int cnt = 0;
        boolean[] seen = new boolean[n];
        int[] it = new int[n];
        int[] stack = new int[n];
        for (int s = 0; s < n; s++) {
            if (seen[s]) continue;
            int sp = 0;
            stack[sp++] = s; seen[s] = true;
            mt.depth(sp);
            while (sp > 0) {
                int v = stack[sp - 1];
                if (it[v] < g.degree(v)) {
                    mt.edgeScans++;
                    int u = g.to(v, it[v]++);
                    if (!seen[u]) { seen[u] = true; stack[sp++] = u; mt.depth(sp); }
                } else {
                    sp--;
                    order[cnt++] = v;           
                }
            }
        }
      
      
        Graph r = g.reversed();
        int[] comp = new int[n];
        java.util.Arrays.fill(comp, -1);
        java.util.Arrays.fill(it, 0);
        int c = 0;
        for (int idx = n - 1; idx >= 0; idx--) {
            int s = order[idx];
            if (comp[s] != -1) continue;
            int sp = 0;
            stack[sp++] = s; comp[s] = c;
            mt.depth(sp);
            while (sp > 0) {
                int v = stack[sp - 1];
                if (it[v] < r.degree(v)) {
                    mt.edgeScans++;
                    int u = r.to(v, it[v]++);
                    if (comp[u] == -1) { comp[u] = c; stack[sp++] = u; mt.depth(sp); }
                } else sp--;
            }
            c++;
        }
        return new SccResult(comp, c);
    }
}
