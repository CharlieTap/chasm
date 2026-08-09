package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.fixture.ir.module.module
import kotlin.test.Test
import kotlin.test.assertEquals

class OptimiserTest {

    @Test
    fun `runs the fusion pipeline when bytecode fusion is disabled`() {

        val executedPasses = mutableListOf<String>()
        val config = RuntimeConfig(
            bytecodeFusion = false,
            gcStrategy = GCStrategy.MANUAL,
        )
        val module = module()

        Compiler(
            config = config,
            module = module,
            control = { _, input -> input.also { executedPasses += "control" } },
            fusion = { _, input -> input.also { executedPasses += "fusion" } },
            frameSlot = { _, input -> input.also { executedPasses += "frameSlot" } },
            jump = { _, input -> input.also { executedPasses += "jump" } },
            gc = { _, input -> input.also { executedPasses += "gc" } },
        )

        assertEquals(
            listOf("control", "fusion", "frameSlot", "jump"),
            executedPasses,
        )
    }
}
