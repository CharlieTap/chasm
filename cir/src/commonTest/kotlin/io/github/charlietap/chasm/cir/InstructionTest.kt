package io.github.charlietap.chasm.cir

import io.github.charlietap.chasm.fixture.cir.allInstructions
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionTest {

    @Test
    fun `every instruction has its own opcode`() {
        val opcodes = allInstructions.map { instruction -> instruction.opcode }

        assertEquals(allInstructions.indices.toList(), opcodes.sorted())
    }
}
