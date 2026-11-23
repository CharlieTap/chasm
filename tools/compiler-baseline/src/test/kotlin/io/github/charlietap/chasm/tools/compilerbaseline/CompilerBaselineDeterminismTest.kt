package io.github.charlietap.chasm.tools.compilerbaseline

import kotlin.test.Test
import kotlin.test.assertEquals

class CompilerBaselineDeterminismTest {

    @Test
    fun `independent generations produce identical baselines`() {
        assertEquals(generate(), generate())
    }

    private fun generate(): String {
        val resourceLoader = CompilerBaselineResourceLoader(
            classLoader = checkNotNull(Thread.currentThread().contextClassLoader),
        )
        val output = StringBuilder()
        compilerBaselineWriter(
            importResolver = EmptyCompilerBaselineImportResolver(),
        ).write(
            CompilerBaselineFixture("contrived", resourceLoader.read("compiler-baseline/contrived.wasm")),
            output,
        )
        return output.toString()
    }
}
