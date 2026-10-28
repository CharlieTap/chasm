package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModuleCompilerBaselineGeneratorTest {

    private val resourceLoader = CompilerBaselineResourceLoader(
        classLoader = checkNotNull(Thread.currentThread().contextClassLoader),
    )

    @Test
    fun `generates CoreMark compiler output`() {
        val generator = ModuleCompilerBaselineGenerator(
            importResolver = CoremarkImportResolver(ChasmHostFunctionAllocator()),
            moduleDecoder = ChasmModuleDecoder(),
            moduleInstantiator = ChasmModuleInstantiator(),
        )

        val baseline = generator.generate(
            CompilerBaselineFixture(
                name = "coremark",
                bytes = resourceLoader.read("compiler-baseline/coremark.wasm"),
            ),
        )

        val module = baseline.modules.single()
        assertEquals(3, baseline.schemaVersion)
        assertEquals("coremark", module.name)
        assertEquals(15, module.functions.size)
        assertTrue(module.functions.all { function -> function.instructions.isNotEmpty() })
    }

    @Test
    fun `checkpoints link only into stores that meter fuel or can be interrupted`() {
        val generator = ModuleCompilerBaselineGenerator(
            importResolver = EmptyCompilerBaselineImportResolver(),
            moduleDecoder = ChasmModuleDecoder(),
            moduleInstantiator = ChasmModuleInstantiator(),
            runtimeConfig = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
            stores = CompilerBaselineStore.ALL,
        )

        val baseline = generator.generate(
            CompilerBaselineFixture(
                name = "contrived",
                bytes = resourceLoader.read("compiler-baseline/contrived.wasm"),
            ),
        )

        val modules = baseline.modules.associateBy(CompilerBaselineModule::name)
        val checkpointDispatchers = mapOf(
            "contrived.default" to emptySet(),
            "contrived.fuel" to setOf("FuelCheckDispatcher"),
            "contrived.interrupt" to setOf("InterruptCheckDispatcher"),
            "contrived.fuel_interrupt" to setOf("FuelAndInterruptCheckDispatcher"),
        )
        for ((name, expected) in checkpointDispatchers) {
            val functions = modules.getValue(name).functions
            val dispatchers = functions.flatMap(CompilerBaselineFunction::dispatchers).toSet()
            assertEquals(expected, dispatchers.filterTo(mutableSetOf()) { it in allCheckpointDispatchers }, name)
            assertEquals(expected.isEmpty(), functions.all { function -> function.unlinkedCheckpoints != null }, name)
        }
    }

    private val allCheckpointDispatchers = setOf(
        "FuelCheckDispatcher",
        "InterruptCheckDispatcher",
        "FuelAndInterruptCheckDispatcher",
    )
}
