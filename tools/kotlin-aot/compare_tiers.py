#!/usr/bin/env python3
"""Compare existing tiers, then optionally collect separate JIT/JFR profiles.

Run the applicable corpus gate before using this after compiler/runtime changes.
"""
import argparse
import json
import pathlib
import random
import statistics
import subprocess

parser = argparse.ArgumentParser(description="Compare the validated STRUCTURED and TYPED tiers in fresh JVMs.")
parser.add_argument("--pairs", type=int, default=5)
parser.add_argument("--output", default="tools/kotlin-aot/build/tier-comparison")
parser.add_argument("--profile", action="store_true")
options = parser.parse_args()
assert options.pairs > 0
root = pathlib.Path(__file__).resolve().parents[2]
output = root / options.output
output.mkdir(parents=True, exist_ok=True)
classpath = (root / "tools/kotlin-aot/build/runtime-classpath.txt").read_text()
heap = ["-Xms1g", "-Xmx8g", "-XX:+UseCompressedOops", "-XX:+UseCompressedClassPointers"]
common = ["--wasm", "benchmark/src/commonMain/resources/benchmark/coremark.wasm",
          "--artifacts", str(output / "artifacts")]
tiers = ["STRUCTURED", "TYPED"]


def run(tier, mode, name, verify=False, jvm=()):
    report = output / (name + ".json")
    command = ["java", *heap, *jvm, "-cp", classpath,
               "io.github.charlietap.chasm.tools.aot.MainKt", "coremark", *common,
               "--tier", tier, "--mode", mode, "--verify", str(verify).lower(),
               "--report", str(report)]
    with report.with_suffix(".log").open("w") as log:
        subprocess.run(command, cwd=root, stdout=log, stderr=subprocess.STDOUT,
                       check=True, timeout=240)
    result = json.loads(report.read_text())
    assert verify or result["placementValid"]
    if mode == "cached":
        assert all(c["cacheHit"] and c["compilationNanos"] == 0
                   for c in result["compilations"])
    if not verify:
        assert result["generatedBlocksExecuted"] == result["generatedInstructionsExecuted"] == 0
    return result


expected = run("STRUCTURED", "interpreter", "verify-interpreter", verify=True)
verification = {}
for tier in tiers:
    verification[tier] = run(tier, "prepare", "verify-" + tier, verify=True)
    for key in ("score", "clockCalls", "memoryBytes", "memorySHA256"):
        assert verification[tier][key] == expected[key], key
    print("Verified", tier, verification[tier]["compilations"][0]["key"], flush=True)
assert verification["STRUCTURED"]["generatedInstructionsExecuted"] == verification["TYPED"]["generatedInstructionsExecuted"]

trials = []
randomizer = random.Random(20260914)
for pair in range(1, options.pairs + 1):
    order = tiers.copy()
    randomizer.shuffle(order)
    for tier in order:
        result = run(tier, "cached", f"pair-{pair}-{tier}")
        trials.append({"pair": pair, "tier": tier, "score": result["score"], "result": result})
        print(pair, tier, result["score"], flush=True)
        (output / "trials.json").write_text(json.dumps(trials, indent=2) + "\n")

summary = {}
for tier in tiers:
    scores = [trial["score"] for trial in trials if trial["tier"] == tier]
    summary[tier] = {"median": statistics.median(scores), "scores": scores}
record = {"pairs": options.pairs, "seed": 20260914, "jvmArguments": heap,
          "verification": verification, "trials": trials, "summary": summary}
(output / "comparison.json").write_text(json.dumps(record, indent=2) + "\n")
print("SUMMARY", json.dumps(summary), flush=True)

if options.profile:
    for tier in tiers:
        print("Profiling", tier, flush=True)
        jvm = ["-XX:+UnlockDiagnosticVMOptions", "-XX:+LogCompilation",
               "-XX:LogFile=" + str(output / (tier + "-hotspot.xml")),
               "-XX:StartFlightRecording=filename=" + str(output / (tier + ".jfr")) +
               ",settings=profile,dumponexit=true"]
        result = run(tier, "cached", "profile-" + tier, jvm=jvm)
        print("Profile complete", tier, result["score"], flush=True)
