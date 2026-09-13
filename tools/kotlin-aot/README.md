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

Each block contains at most 16 lowered operations by default; classes group up
to 192 operations. Small private block methods allow HotSpot to optimize them
independently. The existing dispatch loop enters each block once. Branches,
calls, returns and their original instruction positions remain in the program.
This preserves guest call frames, indirect/host calls and return metadata.
Function entries, branch targets and call boundaries split generated bodies.

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
misses, compilation rollback, block boundaries and unsupported instructions.
Its checked-in `.wasm` can be rebuilt from `compiler/kotlin/src/jvmTest/resources/wasm1.wat`
with `wasm-tools parse`, then checked with `wasm-tools validate --features=mvp`.

## Current limits

This is a JVM prototype validated against the selected Wasm 1.0 corpus and
focused tests, not a claim of complete spec conformance. The source generator
uses common Kotlin, but other platforms still need a build-time compilation and
registration pipeline; JVM class loading is platform specific.

The backend retains ValueStack traffic, linked operand objects, control
dispatch, and ordinary lowering at instantiation. It does not promote values
to virtual registers or generate whole functions with Kotlin control flow.
The prototype driver also retains the Kotlin compiler dependency in cached
processes. Splitting preparation into a separate distributable tool, compact
operand binding, and tuning compilation/code size remain future work.
