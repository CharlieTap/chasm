# JVM prototype results — 13 September 2026

The optional Kotlin source backend passed the selected Wasm 1.0 corpus and
increased median CoreMark score by **31.8%** against the interpreter in this
checkout. This is a CoreMark result on one JVM/machine, not a general application
speedup or evidence for other Kotlin platforms.

Worktree: `chasm-kotlin-aot`, branch `codex/kotlin-source-aot`, based on
`a207e39b2bfb79b110e75387766ad7537081507f`. The comparison enables/disables the
backend in the same implementation and keeps the memory backend and other
runtime settings identical.

The [machine-readable evidence](results/2026-09-13.json) preserves every fixture
name/hash/outcome, all ten timed CoreMark trials, deterministic verification,
compilation statistics, and bytecode inspection. [Reproduction commands](README.md)
are included. Complete local reports and compiled artifacts remain under
`tools/kotlin-aot/build`.

## Correctness

- Corpus revision: `13f6c9049f35df5430516691f9ee0a69ad5d573a`.
- All **209 selected Wasm 1.0 fixtures** pass in preparation, cached execution,
  and interpreter modes: **795 module/test runs and 1,004 assertion steps** in
  each mode, with no failures or skips.
- Selection excludes the repository's existing `stress-test`, `benchmark`,
  and `duration-extra-long` tags. It has no target-name exclusions: both HQX
  and learning-rate scheduling pass.
- The backend compiles all 795 module runs; generated execution is observed
  in every fixture. The instrumented corpus executes 2,015,358,399 operations through
  269,106,459 block calls. These are correctness counters, not timing results.
- Cached corpus execution performs zero Kotlin compilation.
- Six backend tests and 269 existing compiler/invoker/runtime tests pass.
  Formatting and public ABI checks pass; ABI snapshots include the new store
  overload. The official Wasm spec suite was deliberately not run.

CoreMark deterministic verification matches across interpreter, preparation,
and cached execution: score **2.0**, **4** clock calls, **65,536** memory bytes,
and SHA-256
`754b7bce23d2bb90730596ec38f99eb1a6036dbf1a022d947dc197a4ed537169`.
Each real-clock trial also passes CoreMark's internal CRC/duration validation.

## CoreMark performance

Apple M5 Pro, 24 GiB RAM, macOS 26.6.1, Homebrew OpenJDK 25.0.4 arm64 HotSpot,
Kotlin 2.4.0. Five pairs use fresh sequential JVMs with order shuffled by seed
20260913. Both modes use `-Xms1g -Xmx8g`, compressed object/class pointers, and
the existing fastest-core placement support. All ten start/end placement checks
pass. The benchmark uses real clocks, counters disabled, and prepared classes;
no timed generated run compiles Kotlin. No builds or other task benchmarks run
concurrently.

| Pair | Interpreter score | Generated score | First mode |
| --- | ---: | ---: | --- |
| 1 | 1,520.3345 | 2,044.8503 | Generated |
| 2 | 1,543.0908 | 2,047.9214 | Generated |
| 3 | 1,530.8075 | 1,986.7550 | Generated |
| 4 | 1,534.8016 | 2,018.4350 | Interpreter |
| 5 | 1,531.3936 | 1,986.2288 | Interpreter |
| **Median** | **1,531.3936** | **2,018.4350** | |

The median score ratio is **1.3180×**. Generated execution wins all five pairs.
Scores range from 1,520.3–1,543.1 for the interpreter and 1,986.2–2,047.9 for
generated code. CoreMark calibrates its workload, so the approximately 20-second
invocation durations are not fixed-work elapsed-time comparisons.

## Preparation and code size

An additional isolated fresh-JVM preparation run with an empty artifact directory
took **4.00 seconds** before invocation, of which **3.77 seconds** was Kotlin
compilation. This is one observation, reported separately from score trials.
In the ten timed trials, median pre-invocation preparation was **194.8 ms** for
cached generated code and **109.3 ms** for the interpreter; this includes normal
lowering and, for generated code, fingerprinting/binding/loading.

The 7,771-byte CoreMark module lowers to 1,367 instructions. The backend emits
1,024 executor operations in 333 blocks and six classes, retaining 343 control
instructions. Generated Kotlin is 247,829 bytes; class files total 897,834 bytes,
including compiler metadata/debug information.

`javap -c -p` inspection finds no per-instruction `DispatchableInstruction.invoke`
calls in generated classes. Existing inline executor bodies expand into the
block methods. The largest CoreMark block ends at bytecode offset 3,004; even the
largest constructor ends at 3,496. An initial layout put all blocks into a
13–31 KB `invoke` method per class and caused a severe HQX slowdown. Splitting
independently optimizable methods resolved it; only the final layout is included
in the performance comparison.

The deterministic CoreMark run executes 6,774,550 generated instruction
operations through 1,749,357 generated block calls. Retained control dispatches
are additional; these counts must not be presented as an overall dispatch
reduction percentage.

## Interpretation

The proposed intermediate step works: existing executor semantics can be
compiled into Kotlin blocks, remove dispatch between their operations, and
improve CoreMark while retaining the current linked runtime and interpreter.
Stack traffic, operand-object access and control dispatch remain. The measured
code expansion and preparation cost are substantial, and are reasons to keep
this mode optional. Other platforms still require a compilation/registration
pipeline and their own correctness and performance validation.
