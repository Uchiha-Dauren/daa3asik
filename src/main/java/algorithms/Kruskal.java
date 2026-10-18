package algorithms;

import graph.Graph;
import metrics.Metrics;

public final class Kruskal {
    private Kruskal() {}

    public static MstResult run(Graph g, Metrics mt) {
        int n = g.n(), m = g.m();
        long[] keys = new long[m];
        for (int i = 0; i < m; i++) keys[i] = ((long) g.edgeW(i) << 32) | i;
        mergeSort(keys, new long[m], 0, m, mt);

        DSU dsu = new DSU(n, mt);
        long total = 0;
        int taken = 0;
        for (int k = 0; k < m && taken < n - 1; k++) {  
            int i = (int) (keys[k] & 0xFFFFFFFFL);
            mt.edgeScans++;
            if (dsu.union(g.edgeU(i), g.edgeV(i))) {
                total += g.edgeW(i);
                taken++;
            }
        }
        return new MstResult(total, taken, dsu.components());
    }

    private static void mergeSort(long[] a, long[] tmp, int lo, int hi, Metrics mt) {
        if (hi - lo < 2) return;
        int mid = (lo + hi) >>> 1;
        mergeSort(a, tmp, lo, mid, mt);
        mergeSort(a, tmp, mid, hi, mt);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) {
            mt.comparisons++;
            if (a[i] <= a[j]) tmp[k++] = a[i++]; else tmp[k++] = a[j++];
        }
        while (i < mid) tmp[k++] = a[i++];
        while (j < hi) tmp[k++] = a[j++];
        System.arraycopy(tmp, lo, a, lo, hi - lo);
    }
}
