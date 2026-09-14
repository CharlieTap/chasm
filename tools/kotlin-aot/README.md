# Kotlin source execution prototype

This optional backend lowers a module with Chasm's existing compiler, generates
Kotlin block methods containing direct calls to its inline instruction executors,
and loads the compiled JVM classes. Select it with `store(compiler)`; `store()`
continues to select the interpreter. The generator is in `:compiler:kotlin`.
Measured correctness, performance and preparation costs are in [RESULTS.md](RESULTS.md).

```kotlin
val compiler = JvmKotlinProgramCompiler(
    directory = File("build/chasm-kotlin"),
    mode = KotlinCompilationMode.PREPARE,
)
val store = store(compiler)
// Decode, instantiate and invoke with the ordinary embedding APIs.
// At the end: dropStore(store), then compiler.close().
```

`PREPARE` compiles missing artifacts during instantiation. A subsequent process
using `CACHED` only loads prepared classes and reports an instantiation error on
a cache miss. Both modes still perform ordinary module lowering and bind the
current instance's instruction operands. The key includes generated source and
the contents of the runtime/compiler classpath. Rebuilding code can invalidate
artifacts; preparation must use the same classpath as execution.

The default tier keeps scalar values in Kotlin locals across generated branches
and loops. Bounded regions contain at most 96 lowered instructions, with a
separate expansion-cost limit. Eligible functions combine their regions into
one resumable body. Original guest/host call and return instructions remain
installed, so execution saves the frame before a call and resumes afterward.
Single-entry linear loops use Kotlin `while`, `continue` and `break` directly.
Bodies with one entry and a linear sequence of blocks and loops also omit the
outer program-counter switch. Other graphs retain the state machine, including
loops with incoming branches or exception handlers in their interior.
Consistently typed numeric slots use Int, Float or Double locals. Each retains
its original raw word until a numeric write, preserving all bits through
unexecuted paths, raw copies and frame-helper reloads. Mixed numeric slots keep
Long storage. Reports include the counts for each representation.
See [the stage record](../../KOTLIN_AOT_STAGES.md) for eligibility, coverage and
measurements. Earlier tiers remain selectable through `KotlinGenerationTier`.
The typed-local stage passed correctness but regressed CoreMark relative to
STRUCTURED; see the recorded comparisons before choosing a tier for performance.

Reference, table and aggregate operations use the existing frame helpers.
Generated locals are saved before these helpers and reloaded afterward, so
allocations see the canonical reference roots. Allocating instructions carry
the finalized function frame size; preflight restores the complete root range
after a call or catch has reduced the active stack depth. Exception transfers
and reference branches retain their original dispatchers; catch targets are
generated entry points. The backend preserves original call-site and throw
addresses.
Preparation compiles independent classes in batches of at most 128 classes or
1 MiB of source, keeping large modules within a bounded compiler working set.

The generator derives executor names from existing function declarations rather
than copying semantic implementations. Generated JVM source is compiled as a
friend of the invoker module to use its internal inline helpers. Compilation
finishes before memory/data initialization and the module start function. A
failure uses normal instantiation rollback. Unsupported lowered instructions
fail explicitly; there is no silent per-instruction fallback in generated blocks.

## Reproduce the Wasm 1.0 corpus run

Run these commands from the repository root. The corpus revision is pinned by
the existing Gradle configuration. The selector includes every version 1.0
fixture except the existing `stress-test`, `benchmark`, and
`duration-extra-long` tags. It includes `jsquash_hqx` and
`learning_rate_scheduling`, which the default repository corpus task excludes.

```sh
./gradlew :chasm:syncWasmCorpus :chasm:resolveCorpusFixtures \
  :tools:kotlin-aot:writeRuntimeClasspath --no-configuration-cache --max-workers=4
python3 tools/kotlin-aot/select_corpus.py \
  chasm/build/wasm-corpus-fixtures/fixtures.json \
  tools/kotlin-aot/build/corpus-1.0.json
aot_classpath=$(cat tools/kotlin-aot/build/runtime-classpath.txt)
for mode in prepare cached interpreter; do
  java -Xms512m -Xmx8g -cp "$aot_classpath" \
    io.github.charlietap.chasm.tools.aot.MainKt corpus \
    --root chasm/build/wasm-corpus \
    --index tools/kotlin-aot/build/corpus-1.0.json \
    --mode "$mode" --artifacts tools/kotlin-aot/build/artifacts \
    --report "tools/kotlin-aot/build/results/corpus-1.0-$mode.json"
done
```

This uses `ChasmCorpusRunner`, including its expected return values, memory
assertions, host bindings and WASI adapter. Every test gets a fresh instance;
stores are dropped after each fixture. Failures and skips make the tool fail.
Reports contain per-fixture outcomes, compile/cache statistics and dynamic
generated execution counts. The official spec suite is not invoked.

## Include supported Wasm 2.0 and 3.0 fixtures

The corpus command runs every fixture in the supplied index. To include all
versions supported by the repository configuration while retaining its
execution-heavy `esbuild` exclusion:

```sh
python3 tools/kotlin-aot/select_corpus.py \
  chasm/build/wasm-corpus-fixtures/fixtures.json \
  tools/kotlin-aot/build/corpus-supported.json \
  --versions all --exclude-target esbuild
python3 tools/kotlin-aot/run_stage.py --stage stage5-gc-exceptions \
  --index tools/kotlin-aot/build/corpus-supported.json --command-timeout 3600
```

The stage runner requires the corpus and deterministic CoreMark checks to pass
in preparation, cached and interpreter modes before starting timed trials.
The pinned supported index contains 209 Wasm 1.0, 60 Wasm 2.0 and 117 Wasm 3.0
fixtures after this exclusion. It does not enable unsupported SIMD, threads or
memory64 proposals.

## Verify and measure CoreMark

First pass the corpus gate above. Prepare CoreMark, then run deterministic
verification for both backends. The synthetic clock gives a fixed iteration
count; compare score, clock calls, memory length and full memory SHA-256 in the
three reports. These verification durations are not performance results.

```sh
for mode in prepare cached interpreter; do
  java -Xms1g -Xmx8g -cp "$aot_classpath" \
    io.github.charlietap.chasm.tools.aot.MainKt coremark \
    --wasm benchmark/src/commonMain/resources/benchmark/coremark.wasm \
    --mode "$mode" --artifacts tools/kotlin-aot/build/coremark-artifacts \
    --verify true \
    --report "tools/kotlin-aot/build/results/coremark-$mode-verify.json"
done
python3 tools/kotlin-aot/measure_coremark.py
```

The measurement script runs five pairs in fresh sequential JVMs with a fixed
shuffle seed, the same heap/compressed-pointer settings, real clocks, and
generated counters disabled. It checks CPU placement using the repository's
benchmark support and refuses compilation in measured runs. The returned
CoreMark score includes its internal CRC/duration validation. Preparation and
execution durations are reported separately. Do not run builds or other
benchmarks concurrently. Results go under `tools/kotlin-aot/build/results`.

Focused tests, without the official spec suite:

```sh
./gradlew :compiler:kotlin:jvmTest :compiler:jvmTest \
  :executor:invoker:jvmTest :runtime:core:jvmTest \
  --no-configuration-cache --max-workers=4
```

The focused Wasm module covers start, branches/loops, recursion, direct and
indirect calls, host bindings, aliased slot transfers, mutable globals,
loads/stores, memory growth and failure, integer and floating-point operations,
traps, and multiple instances. Other tests cover artifact rebinding, cache
misses, compilation rollback, block boundaries and retained control transfers.
The Wasm 3.0 fixture adds observed automatic GC with live references, cross-call
throw/rethrow, generated catch continuations, tables, typed calls and tail calls.
The checked-in `.wasm` files can be rebuilt from `wasm1.wat` and `wasm3.wat`
in `compiler/kotlin/src/jvmTest/resources/` with `wasm-tools parse`. Validate
`wasm1.wasm` with `wasm-tools validate --features=mvp`; `wasm3.wasm` uses the
validator's supported reference, GC and exception features.

## Current limits

This is a JVM prototype with corpus and focused validation recorded per stage.
These checks do not establish complete spec conformance. The source generator
uses common Kotlin, but other platforms still need a build-time compilation and
registration pipeline; JVM class loading is platform specific.

The backend retains ValueStack traffic at region and helper boundaries, linked
operand objects, runtime call/exception dispatch and ordinary lowering at
instantiation. Mixed numeric slots retain raw Long storage; native locals also
keep a raw word and a flag where required to preserve untyped slot contents.
The prototype driver also retains the Kotlin compiler dependency in cached
processes. Splitting preparation into a separate distributable tool, compact
operand binding, and tuning compilation/code size remain future work.
