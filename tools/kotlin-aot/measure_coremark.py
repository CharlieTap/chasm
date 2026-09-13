#!/usr/bin/env python3
"""Run paired CoreMark trials in fresh sequential JVMs, after preparation."""
import argparse
import json
import pathlib
import random
import statistics
import subprocess

parser = argparse.ArgumentParser()
parser.add_argument("--pairs", type=int, default=5)
parser.add_argument("--seed", type=int, default=20260913)
options = parser.parse_args()
assert options.pairs > 0
root = pathlib.Path(__file__).resolve().parents[2]
output = root / "tools/kotlin-aot/build/results"
output.mkdir(parents=True, exist_ok=True)
classpath = (root / "tools/kotlin-aot/build/runtime-classpath.txt").read_text()
heap = ["-Xms1g", "-Xmx8g", "-XX:+UseCompressedOops", "-XX:+UseCompressedClassPointers"]
command = ["java", *heap, "-cp", classpath, "io.github.charlietap.chasm.tools.aot.MainKt", "coremark"]
common = ["--wasm", "benchmark/src/commonMain/resources/benchmark/coremark.wasm",
          "--artifacts", "tools/kotlin-aot/build/coremark-artifacts", "--verify", "false"]
randomizer = random.Random(options.seed)
trials = []
for pair in range(1, options.pairs + 1):
    order = ["interpreter", "cached"]
    randomizer.shuffle(order)
    for mode in order:
        report = output / f"coremark-{pair}-{mode}.json"
        with report.with_suffix(".log").open("w") as log:
            subprocess.run(command + common + ["--mode", mode, "--report", str(report)],
                           cwd=root, stdout=log, stderr=subprocess.STDOUT, check=True, timeout=180)
        result = json.loads(report.read_text())
        assert result["placementValid"], result
        assert result["generatedBlocksExecuted"] == result["generatedInstructionsExecuted"] == 0
        assert all(c["cacheHit"] and c["compilationNanos"] == 0 for c in result["compilations"])
        trials.append({"pair": pair, "mode": mode, "score": result["score"], "report": report.name})
        print(f"Pair {pair} {mode}: {result['score']:.4f}", flush=True)
scores = {mode: [t["score"] for t in trials if t["mode"] == mode] for mode in ["interpreter", "cached"]}
summary = {
    "pairs": options.pairs, "seed": options.seed, "jvmArguments": heap, "trials": trials,
    "summary": {mode: {"median": statistics.median(values), "min": min(values), "max": max(values)}
                for mode, values in scores.items()},
    "medianScoreRatio": statistics.median(scores["cached"]) / statistics.median(scores["interpreter"]),
}
(output / "coremark-summary.json").write_text(json.dumps(summary, indent=2) + "\n")
print(json.dumps(summary, indent=2))
