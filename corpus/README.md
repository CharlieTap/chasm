# Wasm Corpus Plugin

`corpus` is an included Gradle build that generates Kotlin tests from
[`CharlieTap/wasm-corpus`](https://github.com/CharlieTap/wasm-corpus) fixtures.
The generated tests live under the consuming project's build directory and call
a runtime-owned `CorpusRunner`, so Chasm always tests the local source tree
rather than a published artifact.

## Chasm Tasks

```sh
./gradlew :chasm:generateCorpusTests
./gradlew :chasm:jvmTest --tests '*corpus.generated*'
./gradlew corpus
./gradlew corpus -PwasmCorpus.phase=decoding
./gradlew updateCorpusBaseline
```

Chasm runs invocation fixtures for its configured core Wasm versions while
excluding unsupported features and explicitly excluded targets. The `corpus {}`
block in `chasm/build.gradle.kts` can filter by versions, source languages,
required or excluded features, included or excluded canonical tags, maximum
binary size, maximum test duration, and target names.

The `wasmCorpus.phase` project property overrides the phase configured in the
`corpus {}` block for an individual run. It accepts `decoding`, `validation`,
`instantiation`, or `invocation`, so a decode-only report can be generated
without editing the consuming build.

Each corpus run writes a sorted JSON report to
`chasm/build/reports/corpus/results.json`. There is one result per fixture with
the overall outcome and elapsed nanoseconds for decoding, validation,
instantiation, and execution. The report also records the binary size, fixture
hash, requested phase, test and step counts, and the number of module instances
built. Its top-level metadata captures the corpus configuration, machine,
Gradle runtime, and JVM toolchain used for the tests. Timings are from the
correctness run itself; they are useful for tracking the work performed by each
phase but are not a warmed-up microbenchmark.

Normal corpus runs participate in Gradle's up-to-date checks and build cache.
The selected phase, fixture set, corpus contents, Chasm test runtime, JVM, and
machine details are all inputs, while the raw fixture timings and aggregate
report are declared outputs. An unchanged correctness run can therefore reuse
the measurement that was already produced under the same conditions.

Raw fixture results default to `build/wasm-corpus-results` through the
`corpusResultsDirectory` extension property. The plugin wires that directory
into the generated corpus harness; it does not require any machine-level
environment or system property configuration.

`updateCorpusBaseline` always performs a fresh corpus measurement and copies the
resulting report to the checked-in `baselines/corpus/baseline.json`; it does not
reuse the previous test output or a build-cache entry. CLI phase overrides also
apply to this task. The generated report is retained under `build/reports` so
Gradle continues to own and track its declared output.

`cleanCorpusTests` removes the generated fixture index, test sources, raw
fixture results, and aggregate report. The synced corpus checkout is left in
place so repeated runs do not need to reclone.

## Plugin Shape

The plugin registers:

- `syncWasmCorpus` to clone/fetch and checkout the pinned corpus ref.
- `resolveCorpusFixtures` to invoke the corpus repository's Node CLI.
- `generateCorpusTests` to emit generated Kotlin tests.
- `generateCorpusReport` to aggregate per-fixture results into a JSON report.
- `updateCorpusBaseline` to run the corpus and update its checked-in baseline.
- `corpusMatrix` to print fixture counts by version.
- `cleanCorpusTests` to remove generated fixture metadata and tests.

Generated tests treat `CorpusResult.Skipped` as an explicit skip path and fail
only on `CorpusResult.Failure`.
