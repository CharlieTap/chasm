package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.callInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.f32AbsInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.fusedCall
import io.github.charlietap.chasm.fixture.ir.instruction.fusedF32Abs
import io.github.charlietap.chasm.fixture.ir.instruction.fusedI32Add
import io.github.charlietap.chasm.fixture.ir.instruction.fusedIf
import io.github.charlietap.chasm.fixture.ir.instruction.fusedLocalSet
import io.github.charlietap.chasm.fixture.ir.instruction.fusedSelect
import io.github.charlietap.chasm.fixture.ir.instruction.i32AddInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstOperand
import io.github.charlietap.chasm.fixture.ir.instruction.ifInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetOperand
import io.github.charlietap.chasm.fixture.ir.instruction.localSetDestination
import io.github.charlietap.chasm.fixture.ir.instruction.localSetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.nopInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.selectInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.valueStackDestination
import io.github.charlietap.chasm.fixture.ir.instruction.valueStackOperand
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.functionIndex
import io.github.charlietap.chasm.fixture.ir.module.localIndex
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.module.type
import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
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
        val context = passContext(module)

        val expected = fusedI32Add(
            left = localGetOperand(localIndex(0)),
            right = localGetOperand(localIndex(1)),
            destination = valueStackDestination(),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

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
        val context = passContext(module)

        val expected = fusedF32Abs(
            operand = localGetOperand(localIndex(0)),
            destination = localSetDestination(localIndex(2)),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

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
        val context = passContext(module)

        val expected = fusedI32Add(
            left = localGetOperand(localIndex(0)),
            right = localGetOperand(localIndex(1)),
            destination = localSetDestination(localIndex(2)),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }

    @Test
    fun `can fuse a binary operand instruction where only the right is fusable`() {

        val instructions = listOf(
            nopInstruction(),
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
        val context = passContext(module)

        val expected = listOf(
            nopInstruction(),
            fusedI32Add(
                left = valueStackOperand(),
                right = i32ConstOperand(5),
                destination = valueStackDestination(),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse an if instruction`() {

        val instructions = listOf(
            i32ConstInstruction(5),
            ifInstruction(),
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
        val context = passContext(module)

        val expected = listOf(
            fusedIf(
                operand = i32ConstOperand(5),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse instructions nested in control flow`() {

        val instructions = listOf(
            blockInstruction(
                instructions = listOf(
                    blockInstruction(
                        instructions = listOf(
                            i32ConstInstruction(5),
                            i32ConstInstruction(2),
                            i32AddInstruction(),
                        ),
                    ),
                ),
            ),
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
        val context = passContext(module)

        val expected = listOf(
            blockInstruction(
                instructions = listOf(
                    blockInstruction(
                        instructions = listOf(
                            fusedI32Add(
                                left = i32ConstOperand(5),
                                right = i32ConstOperand(2),
                                destination = valueStackDestination(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse local set`() {

        val instructions = listOf(
            i32ConstInstruction(5),
            localSetInstruction(localIndex(0)),
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
        val context = passContext(module)

        val expected = listOf(
            fusedLocalSet(
                operand = i32ConstOperand(5),
                localIdx = localIndex(0),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse a call instruction`() {

        val instructions = listOf(
            i32ConstInstruction(5),
            localGetInstruction(localIndex(2)),
            localGetInstruction(localIndex(1)),
            callInstruction(functionIndex(0)),
        )
        val recursiveType = functionRecursiveType(
            functionType(
                params = resultType(
                    types = listOf(
                        i32ValueType(),
                        i32ValueType(),
                        i32ValueType(),
                    ),
                ),
            ),
        )
        val module = module(
            types = listOf(
                type(recursiveType = recursiveType),
            ),
            definedTypes = listOf(
                definedType(recursiveType = recursiveType),
            ),
            functions = listOf(
                function(
                    typeIndex = typeIndex(0),
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )
        val context = passContext(module)

        val expected = listOf(
            fusedCall(
                operands = listOf(
                    i32ConstOperand(5),
                    localGetOperand(localIndex(2)),
                    localGetOperand(localIndex(1)),
                ),
                functionIndex = functionIndex(0),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse parametric with just a destination`() {

        val instructions = listOf(
            i32AddInstruction(),
            selectInstruction(),
            localSetInstruction(localIndex(1)),
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
        val context = passContext(module)

        val expected = listOf(
            i32AddInstruction(),
            fusedSelect(
                const = valueStackOperand(),
                val1 = valueStackOperand(),
                val2 = valueStackOperand(),
                destination = localSetDestination(localIndex(1)),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse parametric with const and a destination`() {

        val instructions = listOf(
            i32AddInstruction(),
            i32ConstInstruction(5),
            selectInstruction(),
            localSetInstruction(localIndex(1)),
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
        val context = passContext(module)

        val expected = listOf(
            i32AddInstruction(),
            fusedSelect(
                const = i32ConstOperand(5),
                val1 = valueStackOperand(),
                val2 = valueStackOperand(),
                destination = localSetDestination(localIndex(1)),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse parametric with const val2 and a destination`() {

        val instructions = listOf(
            localGetInstruction(localIndex(2)),
            i32ConstInstruction(5),
            selectInstruction(),
            localSetInstruction(localIndex(1)),
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
        val context = passContext(module)

        val expected = listOf(
            fusedSelect(
                const = i32ConstOperand(5),
                val1 = valueStackOperand(),
                val2 = localGetOperand(localIndex(2)),
                destination = localSetDestination(localIndex(1)),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }

    @Test
    fun `can fuse parametric with const val2 val1 and a destination`() {

        val instructions = listOf(
            i32ConstInstruction(6),
            localGetInstruction(localIndex(2)),
            i32ConstInstruction(5),
            selectInstruction(),
            localSetInstruction(localIndex(1)),
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
        val context = passContext(module)

        val expected = listOf(
            fusedSelect(
                const = i32ConstOperand(5),
                val1 = i32ConstOperand(6),
                val2 = localGetOperand(localIndex(2)),
                destination = localSetDestination(localIndex(1)),
            ),
        )
        val actual = FusionPass(context, module).functions[0].body.instructions

        assertEquals(expected, actual)
    }
}
