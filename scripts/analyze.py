#!/usr/bin/env python3
"""Reads results/results.csv, prints counter/formula ratios and log-log slopes,
writes results/analysis.md and the three plots into results/plots/."""
import csv, math, os
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

rows = list(csv.DictReader(open("results/results.csv")))
for r in rows:
    for k in ("n", "m", "dsu_ops", "edge_scans", "comparisons", "max_depth", "result"):
        r[k] = int(r[k])
    r["time_ms"] = float(r["time_ms"])
    r["dens"] = r["graph"].split("_")[1]

def series(alg, dens):
    return sorted([r for r in rows if r["algorithm"] == alg and r["dens"] == dens], key=lambda r: r["n"])

def slope(xs, ys):
    lx, ly = [math.log(x) for x in xs], [math.log(y) for y in ys]
    mx, my = sum(lx) / len(lx), sum(ly) / len(ly)
    return sum((a - mx) * (b - my) for a, b in zip(lx, ly)) / sum((a - mx) ** 2 for a in lx)

ALGS = ["Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath"]
out = []

# ---- ratios (counter / formula) ----
formulas = {
    "Kruskal": ("dsu_ops / E", lambda r: r["dsu_ops"] / r["m"]),
    "Kruskal ": ("comparisons / (E log2 E)", lambda r: r["comparisons"] / (r["m"] * math.log2(r["m"]))),
    "Prim": ("comparisons / (E log2 V)", lambda r: r["comparisons"] / (r["m"] * math.log2(r["n"]))),
    "Kosaraju": ("edgeScans / (V+E)", lambda r: r["edge_scans"] / (r["n"] + r["m"])),
    "Kahn": ("edgeScans / (V+E)", lambda r: r["edge_scans"] / (r["n"] + r["m"])),
    "DagShortestPath": ("edgeScans / (V+E)", lambda r: r["edge_scans"] / (r["n"] + r["m"])),
}
out.append("## Counter / formula ratios\n")
out.append("| Algorithm | ratio | density | " + " | ".join(str(n) for n in (1000, 3162, 10000, 31623, 100000)) + " |")
out.append("|---|---|---|" + "---|" * 5)
for key, (label, f) in formulas.items():
    alg = key.strip()
    for d in ("sparse", "dense"):
        out.append(f"| {alg} | {label} | {d} | " + " | ".join(f"{f(r):.3f}" for r in series(alg, d)) + " |")

# ---- slopes ----
out.append("\n## Log-log slopes of edgeScans (and other counters) vs n\n")
out.append("| Algorithm | density | slope edgeScans | slope total ops |")
out.append("|---|---|---|---|")
for alg in ALGS:
    for d in ("sparse", "dense"):
        s = series(alg, d)
        xs = [r["n"] for r in s]
        tot = [r["edge_scans"] + r["dsu_ops"] + r["comparisons"] for r in s]
        out.append(f"| {alg} | {d} | {slope(xs, [r['edge_scans'] for r in s]):.3f} | {slope(xs, tot):.3f} |")
out.append("\n## Kruskal: share of edges examined (edgeScans / m)\n")
for d in ("sparse", "dense"):
    out.append(f"- {d}: " + ", ".join(f"{r['edge_scans']/r['m']:.3f}" for r in series("Kruskal", d)))
out.append("\n## Scans per edge (edgeScans / m)\n")
for alg in ALGS:
    for d in ("sparse", "dense"):
        out.append(f"- {alg} {d}: " + ", ".join(f"{r['edge_scans']/r['m']:.3f}" for r in series(alg, d)))
out.append("\n## Median times, ms (not evidence, only context)\n")
out.append("| Algorithm | density | " + " | ".join(str(n) for n in (1000, 3162, 10000, 31623, 100000)) + " |")
out.append("|---|---|" + "---|" * 5)
for alg in ALGS:
    for d in ("sparse", "dense"):
        out.append(f"| {alg} | {d} | " + " | ".join(f"{r['time_ms']:.1f}" for r in series(alg, d)) + " |")
open("results/analysis.md", "w").write("\n".join(out) + "\n")
print("\n".join(out))

# ---- plots ----
os.makedirs("results/plots", exist_ok=True)
colors = {"Kruskal": "tab:blue", "Prim": "tab:orange", "Kosaraju": "tab:green", "Kahn": "tab:red", "DagShortestPath": "tab:purple"}
styles = {"sparse": "-", "dense": "--"}
markers = {"Kruskal": "o", "Prim": "s", "Kosaraju": "D", "Kahn": "^", "DagShortestPath": "x"}
msize = {"Kosaraju": 9, "DagShortestPath": 5}

fig, ax = plt.subplots(figsize=(8, 5.5))
for alg in ALGS:
    for d in ("sparse", "dense"):
        s = series(alg, d)
        ax.plot([r["n"] for r in s], [r["edge_scans"] + r["dsu_ops"] + r["comparisons"] for r in s],
                styles[d], marker=markers[alg], markersize=msize.get(alg, 6), color=colors[alg], label=f"{alg} ({d})")
ax.set_xscale("log"); ax.set_yscale("log")
ax.set_xlabel("n (vertices)"); ax.set_ylabel("operations (edgeScans + dsuOps + comparisons)")
ax.set_title("Plot 1: operations vs n (log-log)"); ax.grid(True, which="both", alpha=.3); ax.legend(fontsize=7, ncol=2)
fig.tight_layout(); fig.savefig("results/plots/1_operations_vs_n.png", dpi=150); plt.close(fig)

fig, ax = plt.subplots(figsize=(8, 5.5))
for alg in ALGS:
    for d in ("sparse", "dense"):
        s = series(alg, d)
        ax.plot([r["n"] for r in s], [r["edge_scans"] / r["m"] for r in s], styles[d], marker=markers[alg], markersize=msize.get(alg, 6),
                color=colors[alg], label=f"{alg} ({d})")
ax.set_xscale("log"); ax.set_xlabel("n (vertices)"); ax.set_ylabel("edgeScans / m")
ax.set_title("Plot 2: scans per edge (flat line = linear work)"); ax.grid(True, which="both", alpha=.3); ax.legend(fontsize=7, ncol=2)
fig.tight_layout(); fig.savefig("results/plots/2_scans_per_edge.png", dpi=150); plt.close(fig)

fig, ax = plt.subplots(figsize=(8, 5.5))
for d, c in (("sparse", "tab:blue"), ("dense", "tab:red")):
    s = series("Kruskal", d)
    ax.plot([r["n"] for r in s], [r["edge_scans"] / r["m"] for r in s], marker="o", color=c, label=f"Kruskal, {d} (m = {4 if d=='sparse' else 20}n)")
ax.set_xscale("log"); ax.set_ylim(0, 1.05); ax.set_xlabel("n (vertices)"); ax.set_ylabel("edgeScans / m")
ax.set_title("Plot 3: share of edges Kruskal examines before the tree is complete"); ax.grid(True, which="both", alpha=.3); ax.legend()
fig.tight_layout(); fig.savefig("results/plots/3_kruskal_edge_share.png", dpi=150); plt.close(fig)
print("plots written")
