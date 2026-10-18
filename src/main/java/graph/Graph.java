package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.StringTokenizer;


public class Graph {
    private final int n;
    private final boolean directed;
    private final int[][] to;
    private final int[][] wt;
    private final int[] deg;
    private int m;
    private int[] eu = new int[16], ev = new int[16], ew = new int[16];

    public Graph(int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("n < 0");
        this.n = n;
        this.directed = directed;
        this.to = new int[n][];
        this.wt = new int[n][];
        this.deg = new int[n];
    }

    public int n() { return n; }
    public int m() { return m; }
    public boolean isDirected() { return directed; }

    public void addEdge(int u, int v, int w) {
        check(u); check(v);
        if (m == eu.length) {
            int c = m * 2;
            eu = Arrays.copyOf(eu, c); ev = Arrays.copyOf(ev, c); ew = Arrays.copyOf(ew, c);
        }
        eu[m] = u; ev[m] = v; ew[m] = w; m++;
        push(u, v, w);
        if (!directed && u != v) push(v, u, w);
    }

    private void push(int u, int v, int w) {
        if (to[u] == null) { to[u] = new int[4]; wt[u] = new int[4]; }
        else if (deg[u] == to[u].length) {
            to[u] = Arrays.copyOf(to[u], deg[u] * 2);
            wt[u] = Arrays.copyOf(wt[u], deg[u] * 2);
        }
        to[u][deg[u]] = v; wt[u][deg[u]] = w; deg[u]++;
    }

    private void check(int v) {
        if (v < 0 || v >= n) throw new IllegalArgumentException("vertex out of range: " + v);
    }

    public int degree(int u) { return deg[u]; }
    public int to(int u, int i) { return to[u][i]; }
    public int weight(int u, int i) { return wt[u][i]; }

    public int edgeU(int i) { return eu[i]; }
    public int edgeV(int i) { return ev[i]; }
    public int edgeW(int i) { return ew[i]; }

    /** Directed graph with every edge turned around. */
    public Graph reversed() {
        Graph r = new Graph(n, true);
        for (int i = 0; i < m; i++) r.addEdge(ev[i], eu[i], ew[i]);
        return r;
    }

    /** Format: first line "n m directed|undirected", then m lines "u v w". */
    public static Graph fromFile(Path path) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int n = Integer.parseInt(st.nextToken());
            int m = Integer.parseInt(st.nextToken());
            String kind = st.nextToken();
            if (!kind.equals("directed") && !kind.equals("undirected"))
                throw new IOException("expected directed|undirected, got " + kind);
            Graph g = new Graph(n, kind.equals("directed"));
            for (int i = 0; i < m; i++) {
                String line = br.readLine();
                if (line == null) throw new IOException("expected " + m + " edges, got " + i);
                st = new StringTokenizer(line);
                g.addEdge(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
                        Integer.parseInt(st.nextToken()));
            }
            return g;
        }
    }
}
