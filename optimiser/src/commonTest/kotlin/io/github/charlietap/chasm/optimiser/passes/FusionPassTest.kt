package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.callInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.f32AbsInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.fusedF32Abs
import io.github.charlietap.chasm.fixture.ir.instruction.fusedI32Add
import io.github.charlietap.chasm.fixture.ir.instruction.i32AddInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32MulInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetOperand
import io.github.charlietap.chasm.fixture.ir.instruction.localSetDestination
import io.github.charlietap.chasm.fixture.ir.instruction.localSetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.valueStackDestination
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.localIndex
import io.github.charlietap.chasm.fixture.ir.module.module
import kotlin.test.Test
import kotlin.test.assertEquals

class FusionPassTest {

    @Test
    fun `can fuse an instructions operands despite no explicit destination`() {

        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            localGetInstruction(localIndex(1)),
            i32AddInstruction(),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )

        val expected = fusedI32Add(
            left = localGetOperand(localIndex(0)),
            right = localGetOperand(localIndex(1)),
            destination = valueStackDestination(),
        )
        val actual = FusionPass(module).functions[0].body.instructions

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }

    @Test
    fun `can fuse an instructions operands with no explicit destination`() {

        val instructions = listOf(
            blockInstruction(
                instructions = listOf(
                    callInstruction(),
                    i32ConstInstruction(3),
                    callInstruction(),
                    i32ConstInstruction(4),
                ),
            ),
            i32ConstInstruction(5),
            i32AddInstruction(),
            i32MulInstruction(),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )

        val expected = listOf(
            blockInstruction(
                instructions = listOf(
                    callInstruction(),
                    i32ConstInstruction(3),
                    callInstruction(),
                    i32ConstInstruction(4),
                ),
            ),
            i32ConstInstruction(5),
            i32AddInstruction(),
            i32MulInstruction(),
        )
        val actual = FusionPass(module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse a unary operand instruction with an explicit destination`() {

        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            f32AbsInstruction(),
            localSetInstruction(localIndex(2)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )

        val expected = fusedF32Abs(
            operand = localGetOperand(localIndex(0)),
            destination = localSetDestination(localIndex(2)),
        )
        val actual = FusionPass(module).functions[0].body.instructions

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }

    @Test
    fun `can fuse a binary operand instruction with an explicit destination`() {

        val instructions = listOf(
            localGetInstruction(localIndex(0)),
            localGetInstruction(localIndex(1)),
            i32AddInstruction(),
            localSetInstruction(localIndex(2)),
        )
        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )

        val expected = fusedI32Add(
            left = localGetOperand(localIndex(0)),
            right = localGetOperand(localIndex(1)),
            destination = localSetDestination(localIndex(2)),
        )
        val actual = FusionPass(module).functions[0].body.instructions

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }
}
