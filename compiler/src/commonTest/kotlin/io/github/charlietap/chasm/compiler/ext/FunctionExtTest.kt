package io.github.charlietap.chasm.compiler.ext

import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.ifInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.loopInstruction
import io.github.charlietap.chasm.fixture.ir.module.function
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionExtTest {

    @Test
    fun `traverseInstructions yields all instructions including nested ones in block instruction`() {
        val nestedInstructions = listOf(
            i32ConstInstruction(2),
            i32ConstInstruction(3),
        )

        val blockInstr = blockInstruction(
            instructions = nestedInstructions,
        )

        val instructions = listOf(
            i32ConstInstruction(1),
            blockInstr,
        )

        val function = function(
            body = expression(instructions),
        )

        val result = function.traverseInstructions().toList()

        val expected = listOf(
            i32ConstInstruction(1),
            blockInstr,
            i32ConstInstruction(2),
            i32ConstInstruction(3),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `traverseInstructions yields all instructions including nested ones in loop instruction`() {
        val nestedInstructions = listOf(
            i32ConstInstruction(2),
            i32ConstInstruction(3),
        )

        val loopInstr = loopInstruction(
            instructions = nestedInstructions,
        )

        val instructions = listOf(
            i32ConstInstruction(1),
            loopInstr,
        )

        val function = function(
            body = expression(instructions),
        )

        val result = function.traverseInstructions().toList()

        val expected = listOf(
            i32ConstInstruction(1),
            loopInstr,
            i32ConstInstruction(2),
            i32ConstInstruction(3),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `traverseInstructions yields all instructions including nested ones in if instruction`() {
        val thenInstructions = listOf(
            i32ConstInstruction(2),
        )

        val elseInstructions = listOf(
            i32ConstInstruction(3),
        )

        val ifInstr = ifInstruction(
            thenInstructions = thenInstructions,
            elseInstructions = elseInstructions,
        )

        val instructions = listOf(
            i32ConstInstruction(1),
            ifInstr,
        )

        val function = function(
            body = expression(instructions),
        )

        val result = function.traverseInstructions().toList()

        val expected = listOf(
            i32ConstInstruction(1),
            ifInstr,
            i32ConstInstruction(2),
            i32ConstInstruction(3),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `traverseInstructions handles deeply nested instructions`() {
        val innerBlock = blockInstruction(
            instructions = listOf(
                i32ConstInstruction(3),
                i32ConstInstruction(4),
            ),
        )

        val innerIf = ifInstruction(
            thenInstructions = listOf(
                i32ConstInstruction(5),
            ),
            elseInstructions = listOf(
                i32ConstInstruction(6),
            ),
        )

        val nestedLoop = loopInstruction(
            instructions = listOf(
                i32ConstInstruction(7),
                innerBlock,
            ),
        )

        val outerBlock = blockInstruction(
            instructions = listOf(
                i32ConstInstruction(2),
                innerIf,
                nestedLoop,
            ),
        )

        val instructions = listOf(
            i32ConstInstruction(1),
            outerBlock,
        )

        val function = function(
            body = expression(instructions),
        )

        val result = function.traverseInstructions().toList()

        val expected = listOf(
            i32ConstInstruction(1),
            outerBlock,
            i32ConstInstruction(2),
            innerIf,
            i32ConstInstruction(5),
            i32ConstInstruction(6),
            nestedLoop,
            i32ConstInstruction(7),
            innerBlock,
            i32ConstInstruction(3),
            i32ConstInstruction(4),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `traverseInstructions handles if instruction with null else branch`() {
        val thenInstructions = listOf(
            i32ConstInstruction(2),
        )

        val ifInstr = ifInstruction(
            thenInstructions = thenInstructions,
            elseInstructions = null,
        )

        val instructions = listOf(
            i32ConstInstruction(1),
            ifInstr,
        )

        val function = function(
            body = expression(instructions),
        )

        val result = function.traverseInstructions().toList()

        val expected = listOf(
            i32ConstInstruction(1),
            ifInstr,
            i32ConstInstruction(2),
        )
        assertEquals(expected, result)
    }
}
