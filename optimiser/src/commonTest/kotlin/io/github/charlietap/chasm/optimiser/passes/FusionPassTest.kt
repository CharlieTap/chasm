package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.fixture.ir.instruction.callInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.f32AbsInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.fusedF32Abs
import io.github.charlietap.chasm.fixture.ir.instruction.fusedI32Add
import io.github.charlietap.chasm.fixture.ir.instruction.i32AddInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstOperand
import io.github.charlietap.chasm.fixture.ir.instruction.localGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetOperand
import io.github.charlietap.chasm.fixture.ir.instruction.localSetDestination
import io.github.charlietap.chasm.fixture.ir.instruction.localSetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.valueStackDestination
import io.github.charlietap.chasm.fixture.ir.instruction.valueStackOperand
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

    @Test
    fun `can fuse a binary operand instruction where only the right is fusable`() {

        val instructions = listOf(
            callInstruction(),
            i32ConstInstruction(5),
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

        val expected = listOf(
            callInstruction(),
            fusedI32Add(
                left = valueStackOperand(),
                right = i32ConstOperand(5),
                destination = valueStackDestination(),
            ),
        )
        val actual = FusionPass(module).functions[0].body.instructions

        assertEquals(expected, actual)
    }
}
