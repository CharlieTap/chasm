package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.fixture.ast.module.localIndex
import io.github.charlietap.chasm.fixture.ir.instruction.fusedI32Add
import io.github.charlietap.chasm.fixture.ir.instruction.i32AddInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetOperand
import io.github.charlietap.chasm.fixture.ir.instruction.localSetDestination
import io.github.charlietap.chasm.fixture.ir.instruction.localSetInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionFuserTest {

    @Test
    fun `can fuse an i32add instruction`() {

        val instructions = listOf(
            localGetInstruction(localIndex(0u)),
            localGetInstruction(localIndex(1u)),
            i32AddInstruction(),
            localSetInstruction(localIndex(2u)),
        )

        val expected = fusedI32Add(
            left = localGetOperand(localIndex(0u)),
            right = localGetOperand(localIndex(1u)),
            destination = localSetDestination(localIndex(2u)),
        )
        val actual = InstructionFuser(instructions)

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }
}
