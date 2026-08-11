package io.github.charlietap.chasm.tools.compilerbaseline

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModuleCompilerBaselineGeneratorTest {

    @Test
    fun `generates CoreMark compiler output`() {
        val resourceLoader = CompilerBaselineResourceLoader(
            classLoader = checkNotNull(Thread.currentThread().contextClassLoader),
        )
        val generator = ModuleCompilerBaselineGenerator(
            importResolver = CoremarkImportResolver(ChasmHostFunctionAllocator()),
            moduleDecoder = ChasmModuleDecoder(),
            moduleInstantiator = ChasmModuleInstantiator(),
            instructionCollectorFactory = DefaultProgramInstructionCollectorFactory(
                tagTranslator = CompilerInstructionTagTranslator(),
            ),
        )

        val baseline = generator.generate(
            CompilerBaselineFixture(
                name = "coremark",
                bytes = resourceLoader.read("compiler-baseline/coremark.wasm"),
            ),
        )

        val module = baseline.modules.single()
        assertEquals(1, baseline.schemaVersion)
        assertEquals("coremark", module.name)
        assertEquals(15, module.functions.size)
        assertTrue(module.functions.all { function -> "control.return" in function.instructions })
    }
}
