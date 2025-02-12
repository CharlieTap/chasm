package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.fixture.ast.module.localIndex
import io.github.charlietap.chasm.fixture.ir.instruction.f32AbsInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.fusedF32Abs
import io.github.charlietap.chasm.fixture.ir.instruction.fusedI32Add
import io.github.charlietap.chasm.fixture.ir.instruction.i32AddInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetOperand
import io.github.charlietap.chasm.fixture.ir.instruction.localSetDestination
import io.github.charlietap.chasm.fixture.ir.instruction.localSetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.valueStackDestination
import kotlin.test.Test
import kotlin.test.assertEquals

class FusionPassTest {

    @Test
    fun `can fuse an instructions operands despite no explicit destination`() {

        val instructions = listOf(
            localGetInstruction(localIndex(0u)),
            localGetInstruction(localIndex(1u)),
            i32AddInstruction(),
        )

        val expected = fusedI32Add(
            left = localGetOperand(localIndex(0u)),
            right = localGetOperand(localIndex(1u)),
            destination = valueStackDestination(),
        )
        val actual = FusionPass(instructions)

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }

    @Test
    fun `can fuse a unary operand instruction with an explicit destination`() {

        val instructions = listOf(
            localGetInstruction(localIndex(0u)),
            f32AbsInstruction(),
            localSetInstruction(localIndex(2u)),
        )

        val expected = fusedF32Abs(
            operand = localGetOperand(localIndex(0u)),
            destination = localSetDestination(localIndex(2u)),
        )
        val actual = FusionPass(instructions)

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }

    @Test
    fun `can fuse a binary operand instruction with an explicit destination`() {

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
        val actual = FusionPass(instructions)

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }
}
