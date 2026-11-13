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

    @Test
    fun `numeric instructions take the first NUMERIC_OPCODE_COUNT opcodes`() {

        val opcodes = allInstructions.filterIsInstance<NumericInstruction>().map { instruction -> instruction.opcode }

        assertEquals((0 until NUMERIC_OPCODE_COUNT).toList(), opcodes.sorted())
    }
}
