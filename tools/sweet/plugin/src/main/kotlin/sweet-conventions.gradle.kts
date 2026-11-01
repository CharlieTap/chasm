import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.plugin.LineExclude
import io.github.charlietap.sweet.plugin.PhaseLimit
import io.github.charlietap.sweet.plugin.WasmTestSuiteGenPlugin
import io.github.charlietap.sweet.plugin.WasmTestSuiteGenPluginExtension

apply<WasmTestSuiteGenPlugin>()

configure<WasmTestSuiteGenPluginExtension> {
    wasmToolsVersion = "1.253.0"

    sources {
        register("core") {
            repositoryUrl = "https://github.com/WebAssembly/testsuite.git"
            revision = "345367358f065375524498749470720d9cdd1418"
            testDirectory = "."
            includes = listOf(
                "*.wast",
                "proposals/threads/*.wast",
                "proposals/wide-arithmetic/*.wast",
                "simd_*/**",
                "**/simd_*",
                "**/relaxed_*",
                "**/*_relaxed_*",
                "**/*64.wast",
                "**/memory64*",
                "table_copy_mixed.wast",
            )
            phaseSupport = SemanticPhase.EXECUTION
            phaseLimits = listOf(
                PhaseLimit(
                    patterns = setOf("proposals/threads/**"),
                    phaseSupport = SemanticPhase.VALIDATION,
                ),
                PhaseLimit(
                    patterns = setOf(
                        "simd_*/**",
                        "**/simd_*",
                        "**/relaxed_*",
                        "**/*_relaxed_*",
                    ),
                    phaseSupport = SemanticPhase.VALIDATION,
                ),
                PhaseLimit(
                    patterns = setOf(
                        "**/*64.wast",
                        "**/memory64*",
                        "table_copy_mixed.wast",
                    ),
                    phaseSupport = SemanticPhase.VALIDATION,
                ),
            )
            lineExcludes = listOf(
                LineExclude(
                    filePath = "proposals/threads/imports.wast",
                    lines = setOf(310, 314, 318, 405, 409, 413),
                ),
                LineExclude(
                    filePath = "proposals/threads/memory.wast",
                    lines = setOf(14, 15),
                ),
            )
        }
    }
}
