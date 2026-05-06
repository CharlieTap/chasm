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
```

The default Chasm slice is intentionally small: Wasm 1.0 invocation fixtures for
`forward_propagation`, `fibonacci`, `binary_search`, and `quick_sort`, excluding
`memory64`, `simd`, and `relaxed-simd`. Broader corpus coverage can be enabled by
changing the `corpus {}` block in `chasm/build.gradle.kts`.

`cleanCorpusTests` removes the generated fixture index and test sources. The
synced corpus checkout is left in place so repeated runs do not need to reclone.

## Plugin Shape

The plugin registers:

- `syncWasmCorpus` to clone/fetch and checkout the pinned corpus ref.
- `resolveCorpusFixtures` to invoke the corpus repository's Node script.
- `generateCorpusTests` to emit generated Kotlin tests.
- `corpusMatrix` to print fixture counts by version.
- `cleanCorpusTests` to remove generated fixture metadata and tests.

Generated tests treat `CorpusResult.Skipped` as an explicit skip path and fail
only on `CorpusResult.Failure`.
