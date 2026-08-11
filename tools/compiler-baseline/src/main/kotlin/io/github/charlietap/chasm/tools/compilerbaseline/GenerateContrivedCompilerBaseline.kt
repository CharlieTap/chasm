package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig

fun main() {
    val resourceLoader = CompilerBaselineResourceLoader(
        classLoader = checkNotNull(Thread.currentThread().contextClassLoader),
    )
    val writer = compilerBaselineWriter(
        importResolver = EmptyCompilerBaselineImportResolver(),
        runtimeConfig = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
    )
    System.out.writer().use { output ->
        writer.write(
            fixture = CompilerBaselineFixture(
                name = "contrived",
                bytes = resourceLoader.read("compiler-baseline/contrived.wasm"),
            ),
            output = output,
        )
    }
}
