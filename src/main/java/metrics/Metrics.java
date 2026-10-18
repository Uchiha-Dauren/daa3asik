package metrics;

public class Metrics {
    public long dsuOps;       // find calls + successful union calls
    public long edgeScans;    // edges looked at
    public long comparisons;  // weights compared
    public long maxDepth;     // deepest explicit stack / queue

    public void depth(long d) { if (d > maxDepth) maxDepth = d; }

    public void reset() { dsuOps = edgeScans = comparisons = maxDepth = 0; }
}
