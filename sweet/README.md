Sweet is a project that automates testing of the official wasm
testsuite against a kotlin multiplatform wasm runtime.

The project includes two modules:

- lib (A binding library)
- plugin (A gradle plugin)


In order to leverage sweet you must first use the binding library to wrap your runtime and provide a standardised interface
which can be used by sweets plugin. An example of this binding can be seen [here](chasm/src/commonTest/kotlin/io/github/charlietap/chasm/script/ChasmScriptRunner.kt)

Once you have created a binding and have an implementation of a ScriptRunner you can configure the gradle plugin.

```kotlin
sweet {
    wasmToolsVersion = "1.222.0"
    testSuiteCommit = "68c6f83f331081ba8aaafae3f89ce20d1cc456fb"
    scriptRunner = "io.github.charlietap.chasm.script.ChasmScriptRunner"
    testPackageName = "io.github.charlietap.chasm.testsuite"
    proposals = listOf(
        "multi-memory",
        "exception-handling",
        "gc",
    )
    excludes = listOf(
        "simd_*/**", "**/simd_*",
        "align.wast", "binary.wast", "data.wast", "elem.wast", "global.wast", "imports.wast", "memory.wast",
        "proposals/exception-handling/binary.wast",
        "proposals/exception-handling/imports.wast",
        "proposals/gc/binary.wast",
        "proposals/multi-memory/data.wast",
    )
}
```

The plugin will do the following:

- Download a copy of the official wasm testsuite at the commit specified in the plugin extension.
 You'll find this artifact in build/wasm-testsuite. The gradle task name for this step is syncWasmTestSuite.
- Download a copy of wasm tools with the version specified in the plugin extension.
You'll find this artifact in build/wasm-tools. The gradle task name for this step is downloadWasmTools
- Run wasmtools on the wasm testsuite to create immediate files that can be used by the script runner.
You'll find this artifact in build/wasm-testsuite-intermediates. The gradle task name for this step is prepareTestSuite
- Generate kotlin multiplatform common tests for each testscript in the testsuite.
You'll find this artifact in build/wasm-testsuite-tests. The gradle task name for this step is generateTests
- Include the newly created common tests as part of your test sources automatically
