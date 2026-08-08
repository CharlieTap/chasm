# AGENTS.md

## Build & Test

- **Fast dev loop:** run `./gradlew test`. This executes the runtime unit
  tests and the official Wasm test suite via the JVM backend, giving quick
  signal without longer-running native targets.
- **Corpus correctness:** run `./gradlew corpus`. This executes the configured
  `wasm-corpus` slice against Chasm on the JVM. Run it before performance
  benchmarks when changing compiler or runtime semantics; focused fixtures and
  the fast test loop are not substitutes for this corpus gate.
- **Full matrix:** invoke the usual platform tasks (e.g. `./gradlew jvmTest`,
  `./gradlew macosArm64Test`, etc.) when you need exhaustive coverage or are
  touching platform-specific code.
- The project is Kotlin Multiplatform; ensure the appropriate toolchains
  (Xcode CLI tools for macOS/iOS, etc.) are installed before running those
  additional tasks.

## Formatting & ABI

- Run `./gradlew fmt` before committing to keep formatting consistent.
- If you’ve changed a public ABI, first check it with `./gradlew
  checkKotlinAbi`. If it reports changes, record them with `./gradlew
  updateKotlinAbi`.
- In Cursor, use the `read_lints` command before finishing to surface IDE
  diagnostics (detekt/ktlint) for the files you edited.

## Commit Messages

- Write subjects as `<area>: <imperative outcome>`, where the area names the
  affected subsystem, such as `decoder`, `decoder/reader`, or `runtime`.
- Keep the subject concise and specific. Prefer `decoder: preserve section
  bounds across nested reads` over generic summaries such as `update decoder`
  or category prefixes such as `refactor(decoder)`.
- A small, self-explanatory commit may use only a subject. For substantive
  changes, add a body that explains:
  1. the previous behavior, bottleneck, or failure and why it mattered;
  2. the implementation approach and any important invariants, fallbacks, or
     compatibility guarantees.
- Describe the resulting behavior rather than narrating the editing process.
- Do not add routine verification such as `./gradlew test`, formatting, linting,
  or standard CI checks to a commit body. These checks are expected for every
  change and do not help explain the commit.
- Include verification evidence only when it adds information needed to
  understand a claim or risk. Performance claims must include the benchmark
  workload, baseline, relevant conditions, and measured result. Compatibility
  or migration claims should likewise include non-obvious validation details
  when they matter.
- Wrap prose at roughly 72 characters and separate distinct ideas into short
  paragraphs.

For example:

```text
decoder: rename DecoderContext to ModuleDecoderContext

The existing context only represents state used while decoding a core
WebAssembly module. Components introduce a wider decoding scope that can
contain multiple core modules, so the generic name becomes ambiguous.

Rename the context before component decoding is introduced. This is a
namespace-only change; decoding behavior is unchanged.
```

A performance commit should preserve the evidence behind its claim:

```text
decoder: optimize binary reader and LEB decoding

Primitive reads previously bound a Result for every byte, while byte-array
and streaming inputs used separate implementations. This added work to the
hot path and prevented LEB decoders from using contiguous input directly.

Unify both inputs behind a buffered reader. Decode byte arrays directly and
preserve SourceReader compatibility through a refill adapter. Use unrolled
LEB fast paths when enough bytes are contiguous and fall back near a refill
boundary.

Sequential corpus decode comparisons against 1.4.7 improved the byte-array
path from 1.179 s to 0.690 s and the SourceReader path from 1.247 s to
0.677 s, reductions of 41.5% and 45.7% respectively.
```
