# Sweet

Sweet generates Kotlin Multiplatform tests from one or more WebAssembly WAST
repositories.

The included build contains:

- `lib`: serializable bindings for the `wasm-tools json-from-wast` schema.
- `plugin`: Gradle tasks which synchronize repositories, convert WAST scripts,
  and generate common Kotlin tests.

A runtime supplies a `ScriptRunner` that maps Sweet commands onto its own
decode, validation, and execution APIs. Chasm's implementation is in
[`ChasmScriptRunner.kt`](../../chasm/src/commonTest/kotlin/io/github/charlietap/chasm/script/ChasmScriptRunner.kt).

## Configuration

Suite repositories are named sources. Each source has an independent Git URL,
revision, repository-relative test directory, file selection, and maximum
semantic phase.

```kotlin
import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.plugin.LineExclude
import io.github.charlietap.sweet.plugin.PhaseLimit

sweet {
    wasmToolsVersion = "1.253.0"
    scriptRunner = "example.runtime.ExampleScriptRunner"
    testPackageName = "example.runtime.testsuite"

    sources {
        register("core") {
            repositoryUrl = "https://github.com/WebAssembly/testsuite.git"
            revision = "<full commit>"
            testDirectory = "."
            includes = listOf(
                "*.wast",
                "proposals/threads/*.wast",
            )
            phaseSupport = SemanticPhase.EXECUTION
            phaseLimits = listOf(
                PhaseLimit(
                    patterns = setOf("proposals/threads/**"),
                    phaseSupport = SemanticPhase.VALIDATION,
                ),
            )
            lineExcludes = listOf(
                LineExclude(
                    filePath = "proposals/threads/imports.wast",
                    lines = setOf(310, 314),
                    phase = SemanticPhase.VALIDATION,
                ),
            )
        }

        register("componentModel") {
            repositoryUrl =
                "https://github.com/WebAssembly/component-model.git"
            revision = "<full commit>"
            testDirectory = "test"
            includes = listOf("**/*.wast")
            phaseSupport = SemanticPhase.DECODING
        }
    }
}
```

`includes`, `excludes`, and `PhaseLimit.patterns` are matched against normalized
forward-slash paths relative to `testDirectory`. When multiple phase limits
match, Sweet chooses the lowest phase. A phase limit can reduce source support,
but cannot raise it.

`LineExclude.filePath` uses the same source-relative path. Its one-based command
lines are passed to the runtime's `ScriptRunner`, which skips them without
changing the parsed script. A line exclusion with no phase applies at every
phase.

Use full commits for reproducible suites. The repository revision and
`wasmToolsVersion` should be upgraded together because newer WAST syntax may
require a newer converter.

## Tasks

Sweet registers one isolated task chain per source. A source named `core`
creates:

```text
syncCoreTestSuite
prepareCoreTestSuite
generateCoreTests
```

The aggregate lifecycle tasks retain stable names:

```text
syncWasmTestSuite
prepareTestSuite
generateTests
testMatrix
```

Compiling Kotlin tests depends on `generateTests`, and generated sources are
added to `commonTest`, so the same suites run on every configured KMP test
target.

## Outputs

Each source owns separate output directories:

```text
build/sweet/repositories/<source>/
build/sweet/intermediates/<source>/<relative-wast-path>/
build/generated/sweet/<source>/
```

Sweet preserves the complete path below `testDirectory`. Files such as
`wasm-tools/resources.wast` and `wasmtime/resources.wast` therefore cannot
overwrite one another.

`wasm-tools json-from-wast` uses the historical JSON command name `module` for
both Core modules and components. Sweet preserves that external schema. A
runtime that supports both binary layers should inspect the Wasm preamble when
choosing its decoder.
