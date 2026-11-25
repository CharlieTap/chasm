package io.github.charlietap.chasm.compiler.backend.dex

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DevirtualisedCallLoweringTest {

    private val module = decodeModule(fixture("devirtualisation"))
    private val code = ModuleCompiler(module, module.typeResolver(), recordProvenance = false, devirtualiseIndirectCalls = true)
        .getOrThrow { error -> IllegalStateException(error.toString()) }

    @Test
    fun `the fixture devirtualises its indirect calls`() {
        val directCalls = code.functions.sumOf { function -> function.instructions.count { instruction -> instruction is ControlInstruction.Call } }

        assertTrue(directCalls > 0)
    }

    @Test
    fun `devirtualised calls lower whole and in every forced split window`() {
        val windows = listOf(null) + (1..8).toList()

        windows.forEach { window ->
            val generated = lower(module, code, DexOptions(byteArrayBaseOffset = 16L, splitInstructions = window))

            assertTrue(generated.dex.isNotEmpty(), "window $window")
            assertEquals(emptyList(), zeroOffsetBranches(generated.dex), "window $window")
        }
    }
}
