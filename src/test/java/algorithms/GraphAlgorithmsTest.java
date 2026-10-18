package algorithms;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import graph.Graph;
import graph.GraphGenerator;
import metrics.Metrics;

class GraphAlgorithmsTest {

    @Test
    void dsuAgreesWithReference() {
        Random rnd = new Random(1);
        int n = 300;
        DSU dsu = new DSU(n);
        int[] ref = new int[n];
        for (int i = 0; i < n; i++) ref[i] = i;
        for (int k = 0; k < 400; k++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            dsu.union(a, b);
            int ra = ref[a], rb = ref[b];
            if (ra != rb) for (int i = 0; i < n; i++) if (ref[i] == rb) ref[i] = ra;
        }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j += 7)
                assertEquals(ref[i] == ref[j], dsu.find(i) == dsu.find(j));
        assertEquals(Arrays.stream(ref).distinct().count(), dsu.components());
    }

    @Test
    void dsuCountsUnions() {
        DSU d = new DSU(5);
        assertTrue(d.union(0, 1));
        assertFalse(d.union(1, 0));
        assertEquals(1, d.unionCalls);
        assertEquals(4, d.components());
    }

   
    @Test
    void kruskalAndPrimAgreeOnRandomGraphs() {
        Random rnd = new Random(7);
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(60);
            Graph g = new Graph(n, false);
            for (int i = 1; i < n; i++) g.addEdge(rnd.nextInt(i), i, 1 + rnd.nextInt(50));
            int extra = rnd.nextInt(3 * n);
            for (int k = 0; k < extra; k++) g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1 + rnd.nextInt(50)); // loops + parallel edges
            MstResult a = Kruskal.run(g, new Metrics()), b = Prim.run(g, new Metrics());
            assertEquals(a.totalWeight(), b.totalWeight());
            assertEquals(n - 1, a.treeEdges());
            assertEquals(n - 1, b.treeEdges());
        }
    }

    @Test
    void disconnectedGraphGivesForest() throws IOException {
        Graph g = Graph.fromFile(Path.of("data/disconnected.txt"));   // 8 vertices: {0,1,2} {3,4,5} {6} {7}
        MstResult k = Kruskal.run(g, new Metrics()), p = Prim.run(g, new Metrics());
        assertEquals(4, k.components());
        assertEquals(4, p.components());
        assertEquals(6 + 4, k.totalWeight());   // (1-2:2 + 0-1:4) + (3-4:1 + 4-5:3)
        assertEquals(k.totalWeight(), p.totalWeight());
        assertEquals(4, k.treeEdges());
    }

    @Test
    void emptyAndSingleVertexDoNotCrash() {
        assertEquals(0, Kruskal.run(new Graph(0, false), new Metrics()).totalWeight());
        assertEquals(0, Prim.run(new Graph(1, false), new Metrics()).totalWeight());
        assertEquals(0, Kosaraju.run(new Graph(0, true), new Metrics()).count());
    }

    // ---------- SCC ----------
    @Test
    void kosarajuMatchesBruteForce() {
        Random rnd = new Random(3);
        for (int t = 0; t < 60; t++) {
            int n = 1 + rnd.nextInt(200);
            int m = rnd.nextInt(3 * n);
            Graph g = new Graph(n, true);
            for (int i = 0; i < m; i++) g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1);
            SccResult r = Kosaraju.run(g, new Metrics());
            boolean[][] reach = new boolean[n][];
            Graph rev = g.reversed();
            boolean[][] back = new boolean[n][];
            for (int v = 0; v < n; v++) { reach[v] = bfs(g, v); back[v] = bfs(rev, v); }
            Set<Integer> ids = new HashSet<>();
            for (int u = 0; u < n; u++) {
                ids.add(r.comp()[u]);
                for (int v = 0; v < n; v++) {
                    boolean same = reach[u][v] && back[u][v];   // BFS from u in both directions
                    assertEquals(same, r.comp()[u] == r.comp()[v]);
                }
            }
            assertEquals(ids.size(), r.count());
        }
    }

    private static boolean[] bfs(Graph g, int s) {
        boolean[] seen = new boolean[g.n()];
        ArrayDeque<Integer> q = new ArrayDeque<>();
        q.add(s); seen[s] = true;
        while (!q.isEmpty()) {
            int v = q.poll();
            for (int i = 0; i < g.degree(v); i++) {
                int u = g.to(v, i);
                if (!seen[u]) { seen[u] = true; q.add(u); }
            }
        }
        return seen;
    }

    @Test
    void sccOnPathOf100000VerticesIsStackSafe() {
        int n = 100_000;
        Graph g = new Graph(n, true);
        for (int i = 0; i + 1 < n; i++) g.addEdge(i, i + 1, 1);
        Metrics mt = new Metrics();
        SccResult r = assertDoesNotThrow(() -> Kosaraju.run(g, mt));
        assertEquals(n, r.count());
        assertEquals(n, mt.maxDepth);  
    }

   
    @Test
    void kahnValidOnDagAndDetectsCycle() throws IOException {
        Graph dag = Graph.fromFile(Path.of("data/dag.txt"));
        TopoResult t = Kahn.run(dag, new Metrics());
        assertFalse(t.hasCycle());
        assertTrue(Kahn.isValidOrder(dag, t));

        Graph cyc = Graph.fromFile(Path.of("data/cyclic.txt"));
        TopoResult c = Kahn.run(cyc, new Metrics());
        assertTrue(c.hasCycle());
        assertFalse(Kahn.isValidOrder(cyc, c));
    }

    @Test
    void kahnValidOnGeneratedDag() {
        Graph g = GraphGenerator.dag(2000, 8000);
        assertTrue(Kahn.isValidOrder(g, Kahn.run(g, new Metrics())));
    }

    @Test
    void dagShortestPathMatchesBellmanFord() {
        Random rnd = new Random(11);
        for (int t = 0; t < 200; t++) {
            int n = 1 + rnd.nextInt(25);
            int[] rank = new int[n];
            for (int i = 0; i < n; i++) rank[i] = i;
            for (int i = n - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int x = rank[i]; rank[i] = rank[j]; rank[j] = x; }
            Graph g = new Graph(n, true);
            int m = rnd.nextInt(3 * n + 1);
            for (int k = 0; k < m; k++) {
                int a = rnd.nextInt(n), b = rnd.nextInt(n);
                if (a == b) continue;
                if (rank[a] > rank[b]) { int x = a; a = b; b = x; }
                g.addEdge(a, b, rnd.nextInt(21) - 10);
            }
            DagShortestPath r = DagShortestPath.run(g, new Metrics());
            long[] bf = bellmanFord(g);
            assertArrayEquals(bf, r.dist);
            for (int v = 0; v < n; v++) {         
                List<Integer> p = r.pathTo(v);
                long len = 0;
                for (int i = 0; i + 1 < p.size(); i++) len += cheapest(g, p.get(i), p.get(i + 1), r.dist);
                assertEquals(r.dist[v], len);
            }
        }
    }

    private static long cheapest(Graph g, int u, int v, long[] dist) {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < g.degree(u); i++) if (g.to(u, i) == v) best = Math.min(best, g.weight(u, i));
        return best;
    }

    private static long[] bellmanFord(Graph g) {
        int n = g.n();
        boolean[] hasIn = new boolean[n];
        for (int i = 0; i < g.m(); i++) hasIn[g.edgeV(i)] = true;
        long[] d = new long[n];
        for (int v = 0; v < n; v++) d[v] = hasIn[v] ? DagShortestPath.INF : 0;
        for (int round = 0; round < n; round++)
            for (int i = 0; i < g.m(); i++)
                if (d[g.edgeU(i)] != DagShortestPath.INF && d[g.edgeU(i)] + g.edgeW(i) < d[g.edgeV(i)])
                    d[g.edgeV(i)] = d[g.edgeU(i)] + g.edgeW(i);
        return d;
    }

    @Test
    void dagShortestPathRejectsCycle() throws IOException {
        Graph cyc = Graph.fromFile(Path.of("data/cyclic.txt"));
        assertThrows(IllegalArgumentException.class, () -> DagShortestPath.run(cyc, new Metrics()));
    }

    // ---------- counters ----------
    @Test
    void scanCountersMatchTheory() {
        Graph d = GraphGenerator.directed(1000, 4000);
        Metrics k = new Metrics();
        Kosaraju.run(d, k);
        assertEquals(2L * d.m(), k.edgeScans);
        Graph dag = GraphGenerator.dag(1000, 4000);
        Metrics h = new Metrics();
        Kahn.run(dag, h);
        assertEquals(dag.m(), h.edgeScans);
    }
}
