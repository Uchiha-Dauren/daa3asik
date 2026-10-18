package algorithms;

import metrics.Metrics;


public class DSU {
    private int[] parent;
    private byte[] rank;
    private int components;
    public long findCalls, unionCalls;   // unionCalls = successful unions only
    private final Metrics metrics;

    public DSU(int n) { this(n, null); }

    public DSU(int n, Metrics metrics) {
        this.metrics = metrics;
        makeSet(n);
    }

    public final void makeSet(int n) {
        parent = new int[n];
        rank = new byte[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        components = n;
        findCalls = unionCalls = 0;
    }

    public int find(int x) {
        findCalls++;
        if (metrics != null) metrics.dsuOps++;
        int root = x;
        while (parent[root] != root) root = parent[root];
        while (parent[x] != root) {          
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }


    public boolean union(int x, int y) {
        int a = find(x), b = find(y);
        if (a == b) return false;
        if (rank[a] < rank[b]) { int t = a; a = b; b = t; }   
        parent[b] = a;
        if (rank[a] == rank[b]) rank[a]++;
        components--;
        unionCalls++;
        if (metrics != null) metrics.dsuOps++;
        return true;
    }

    public int components() { return components; }
}
