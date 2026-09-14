#!/usr/bin/env python3
"""Validate a built stage, then measure it; never benchmark a failing stage."""
import argparse
import collections
import hashlib
import json
import pathlib
import subprocess
import sys

parser = argparse.ArgumentParser()
parser.add_argument("--stage", required=True)
parser.add_argument("--pairs", type=int, default=5)
parser.add_argument("--index", default="tools/kotlin-aot/build/corpus-1.0.json")
parser.add_argument("--command-timeout", type=int, default=1200)
options = parser.parse_args()
assert options.stage.replace("-", "").replace("_", "").isalnum()
root = pathlib.Path(__file__).resolve().parents[2]
output = root / "tools/kotlin-aot/build/stages" / options.stage
output.mkdir(parents=True, exist_ok=True)
artifacts = output / "artifacts"
classpath = (root / "tools/kotlin-aot/build/runtime-classpath.txt").read_text()
java = ["java", "-Xms1g", "-Xmx8g", "-XX:+UseCompressedOops",
        "-XX:+UseCompressedClassPointers", "-cp", classpath,
        "io.github.charlietap.chasm.tools.aot.MainKt"]


def run(command, log):
    print("Running", log.name, flush=True)
    with log.open("w") as stream:
        subprocess.run(command, cwd=root, stdout=stream, stderr=subprocess.STDOUT,
                       check=True, timeout=options.command_timeout)


corpus = {}
for mode in ("prepare", "cached", "interpreter"):
    report = output / f"corpus-{mode}.json"
    run(java + ["corpus", "--root", "chasm/build/wasm-corpus", "--index", options.index,
                "--mode", mode, "--artifacts", str(artifacts), "--report", str(report)],
        report.with_suffix(".log"))
    data = json.loads(report.read_text())
    assert len(data["results"]) == data["selected"]
    assert all(r["status"] == "passed" for r in data["results"])
    if mode == "cached":
        assert all(c["cacheHit"] and c["compilationNanos"] == 0
                   for r in data["results"] for c in r["compilations"])
    corpus[mode] = data
    print(f"{mode}: {data['selected']} corpus fixtures passed", flush=True)

verification = {}
for mode in ("prepare", "cached", "interpreter"):
    report = output / f"verify-{mode}.json"
    run(java + ["coremark", "--wasm", "benchmark/src/commonMain/resources/benchmark/coremark.wasm",
                "--mode", mode, "--verify", "true", "--artifacts", str(artifacts), "--report", str(report)],
        report.with_suffix(".log"))
    verification[mode] = json.loads(report.read_text())
expected = verification["interpreter"]
assert expected["memoryBytes"] > 0
for result in verification.values():
    for key in ("score", "clockCalls", "memoryBytes", "memorySHA256"):
        assert result[key] == expected[key], (key, result, expected)
print("CoreMark output, clock calls and memory match", flush=True)

subprocess.run([sys.executable, "tools/kotlin-aot/measure_coremark.py", "--pairs", str(options.pairs),
                "--output", str(output), "--artifacts", str(artifacts)], cwd=root, check=True)
summary = json.loads((output / "coremark-summary.json").read_text())
for trial in summary["trials"]:
    trial["result"] = json.loads((output / trial["report"]).read_text())
selected_index = root / options.index
selected_fixtures = json.loads(selected_index.read_text())
selected_ids = {(f["version"], f["name"]) for f in selected_fixtures}
resolved_index = root / "chasm/build/wasm-corpus-fixtures/fixtures.json"
omitted_fixtures = [
    {"version": f["version"], "name": f["name"]}
    for f in json.loads(resolved_index.read_text())
    if (f["version"], f["name"]) not in selected_ids
]
record = {
    "corpusSelection": {
        "index": options.index,
        "indexSHA256": hashlib.sha256(selected_index.read_bytes()).hexdigest(),
        "versions": dict(collections.Counter(f["version"] for f in selected_fixtures)),
        "omittedFromResolvedIndex": omitted_fixtures,
    },
    "stage": options.stage,
    "parentCommit": subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=root, text=True).strip(),
    "trackedDiffSHA256": hashlib.sha256(subprocess.check_output(["git", "diff", "HEAD"], cwd=root)).hexdigest(),
    "corpusCommit": subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=root / "chasm/build/wasm-corpus", text=True).strip(),
    "verification": verification,
    "coremark": summary,
    "corpus": {mode: {
        "selected": data["selected"],
        "tests": sum(r["tests"] for r in data["results"]),
        "steps": sum(r["steps"] for r in data["results"]),
        "generatedBlocksExecuted": sum(r["generatedBlocksExecuted"] for r in data["results"]),
        "generatedInstructionsExecuted": sum(r["generatedInstructionsExecuted"] for r in data["results"]),
        "results": [{k: v for k, v in r.items() if k in ("name", "version", "sha256", "status")}
                    for r in data["results"]],
    } for mode, data in corpus.items()},
}
destination = root / "tools/kotlin-aot/results/stages" / (options.stage + ".json")
destination.parent.mkdir(parents=True, exist_ok=True)
destination.write_text(json.dumps(record, indent=2) + "\n")
print("Stage recorded:", destination, flush=True)
