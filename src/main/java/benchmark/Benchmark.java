package benchmark;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Supplier;

import algorithms.DagShortestPath;
import algorithms.Kahn;
import algorithms.Kosaraju;
import algorithms.Kruskal;
import algorithms.Prim;
import algorithms.TopoResult;
import graph.Graph;
import graph.GraphGenerator;
import metrics.Metrics;

public class Benchmark {
    static final int[] SIZES = {1000, 3162, 10000, 31623, 100000};
    static final int[] DENSITY = {4, 20};
    static final String[] DNAME = {"sparse", "dense"};

    // reference values from the assignment: {MST weight, SCC count, sum of DAG distances}
    static final long[][] REF = {
            {147970, 27, 396274}, {30363, 1, 252411},
            {468911, 137, 1271911}, {97589, 1, 723157},
            {1486139, 373, 3941828}, {309550, 1, 2381342},
            {4715515, 1188, 12551891}, {968517, 1, 7376612},
            {15012094, 3923, 40096539}, {3056681, 1, 23512087}};

    record Run(Metrics mt, long result, double ms) {}

    public static void main(String[] args) throws IOException {
        Path out = Path.of(args.length > 0 ? args[0] : "results/results.csv");
        if (out.getParent() != null) Files.createDirectories(out.getParent());
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(out))) {
            w.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");
            int row = 0;
            for (int si = 0; si < SIZES.length; si++) {
                for (int di = 0; di < DENSITY.length; di++, row++) {
                    int n = SIZES[si], m = DENSITY[di] * n;
                    String name = n + "_" + DNAME[di];
                    long[] ref = REF[row];
                    System.out.println("== " + name + "  (n=" + n + ", m=" + m + ")");

                    Graph und = GraphGenerator.undirected(n, m);
                    Graph dir = GraphGenerator.directed(n, m);
                    Graph dag = GraphGenerator.dag(n, m);

                    Run kr = measure(() -> { Metrics mt = new Metrics(); return new Run(mt, Kruskal.run(und, mt).totalWeight(), 0); });
                    Run pr = measure(() -> { Metrics mt = new Metrics(); return new Run(mt, Prim.run(und, mt).totalWeight(), 0); });
                    Run ko = measure(() -> { Metrics mt = new Metrics(); return new Run(mt, Kosaraju.run(dir, mt).count(), 0); });
                    Run ka = measure(() -> { Metrics mt = new Metrics(); return new Run(mt, Kahn.run(dag, mt).size(), 0); });
                    Run ds = measure(() -> { Metrics mt = new Metrics(); return new Run(mt, DagShortestPath.run(dag, mt).sumOfDistances(), 0); });

                    // ---- self checks: fail loudly ----
                    check(kr.result == pr.result, name + ": Kruskal " + kr.result + " != Prim " + pr.result);
                    check(kr.result == ref[0], name + ": MST " + kr.result + " != reference " + ref[0]);
                    check(ko.result == ref[1], name + ": SCC " + ko.result + " != reference " + ref[1]);
                    check(ds.result == ref[2], name + ": DAG distances " + ds.result + " != reference " + ref[2]);
                    TopoResult t = Kahn.run(dag, new Metrics());
                    check(Kahn.isValidOrder(dag, t), name + ": invalid topological order");
                    check(ka.result == n, name + ": Kahn ordered " + ka.result + " of " + n + " vertices");
                    check(ko.mt.edgeScans == 2L * m, name + ": Kosaraju scans " + ko.mt.edgeScans + " != 2m");
                    check(ka.mt.edgeScans == m, name + ": Kahn scans " + ka.mt.edgeScans + " != m");
                    check(ds.mt.edgeScans == m, name + ": DAG scans " + ds.mt.edgeScans + " != m");

                    write(w, "Kruskal", name, n, m, kr);
                    write(w, "Prim", name, n, m, pr);
                    write(w, "Kosaraju", name, n, m, ko);
                    write(w, "Kahn", name, n, m, ka);
                    write(w, "DagShortestPath", name, n, m, ds);
                    w.flush();
                    System.out.println("   all self-checks passed (MST=" + kr.result + ", SCC=" + ko.result
                            + ", DAGsum=" + ds.result + ")");
                }
            }
        }
        System.out.println("OK: wrote " + out);
    }

    static Run measure(Supplier<Run> task) {
        double[] t = new double[5];
        Run first = null;
        for (int i = 0; i < 5; i++) {
            long s = System.nanoTime();
            Run r = task.get();
            t[i] = (System.nanoTime() - s) / 1e6;
            if (first == null) first = r;
            else if (first.mt.edgeScans != r.mt.edgeScans || first.mt.comparisons != r.mt.comparisons)
                throw new IllegalStateException("counters are not deterministic");
        }
        Arrays.sort(t);
        return new Run(first.mt, first.result, t[2]);
    }

    static void write(PrintWriter w, String alg, String name, int n, int m, Run r) {
        w.printf(java.util.Locale.ROOT, "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d%n", alg, name, n, m, r.ms,
                r.mt.dsuOps, r.mt.edgeScans, r.mt.comparisons, r.mt.maxDepth, r.result);
    }

    static void check(boolean ok, String msg) {
        if (!ok) {
            System.err.println("BENCHMARK FAILED: " + msg);
            System.exit(1);
        }
    }
}
