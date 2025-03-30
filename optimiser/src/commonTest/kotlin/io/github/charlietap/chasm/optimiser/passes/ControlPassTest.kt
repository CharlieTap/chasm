package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brIfInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.brInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.i32AddInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.ifInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.jumpAdjustingInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.jumpIfInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.jumpIfNotInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.jumpInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localGetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localSetInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.localTeeInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.loopInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.returnFunctionInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.signedTypeIndexBlockType
import io.github.charlietap.chasm.fixture.ir.instruction.stackAdjustment
import io.github.charlietap.chasm.fixture.ir.instruction.valueBlockType
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals

class ControlPassTest {

    @Test
    fun `can unnest one level of block instructions into a flat list of instructions`() {

        val instructions = listOf(
            i32ConstInstruction(),
            blockInstruction(
                blockType = valueBlockType(i32ValueType()),
                instructions = listOf(
                    i32AddInstruction(),
                    brInstruction(labelIndex(0)),
                    localGetInstruction(),
                    localTeeInstruction(),
                ),
            ),
            localSetInstruction(),
        )
        val module = module(
            definedTypes = listOf(
                functionType().definedType(),
            ),
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
            i32ConstInstruction(),
            i32AddInstruction(),
            jumpAdjustingInstruction(
                offset = 3,
                stackAdjustment(
                    depth = 1,
                    keep = 1,
                ),
            ),
            localGetInstruction(),
            localTeeInstruction(),
            localSetInstruction(),
            returnFunctionInstruction(),
        )

        val actual = ControlFlowPass(context, module).functions[0].body.instructions
        assertEquals(expected, actual)
    }

    @Test
    fun `can unnest two levels of block instructions into a flat list of instructions`() {

        val instructions = listOf(
            i32ConstInstruction(),
            blockInstruction(
                blockType = valueBlockType(i32ValueType()),
                instructions = listOf(
                    i32AddInstruction(),
                    brInstruction(labelIndex(0)),
                    blockInstruction(
                        blockType = valueBlockType(i32ValueType()),
                        instructions = listOf(
                            i32ConstInstruction(),
                            brInstruction(labelIndex(1)),
                            localGetInstruction(),
                        ),
                    ),
                    i32ConstInstruction(),
                ),
            ),
            localSetInstruction(),
        )
        val module = module(
            definedTypes = listOf(
                functionType().definedType(),
            ),
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
            i32ConstInstruction(),
            i32AddInstruction(),
            jumpAdjustingInstruction(
                offset = 5,
                stackAdjustment(
                    depth = 1,
                    keep = 1,
                ),
            ),
            i32ConstInstruction(),
            jumpAdjustingInstruction(
                offset = 3,
                stackAdjustment(
                    depth = 1,
                    keep = 1,
                ),
            ),
            localGetInstruction(),
            i32ConstInstruction(),
            localSetInstruction(),
            returnFunctionInstruction(),
        )

        val actual = ControlFlowPass(context, module).functions[0].body.instructions
        assertEquals(expected, actual)
    }

    @Test
    fun `can unnest an if expression into a flat list of instructions`() {

        val instructions = listOf(
            i32ConstInstruction(),
            ifInstruction(
                blockType = valueBlockType(i32ValueType()),
                thenInstructions = listOf(
                    i32ConstInstruction(),
                    i32ConstInstruction(),
                ),
            ),
            localSetInstruction(),
        )
        val module = module(
            definedTypes = listOf(
                functionType().definedType(),
            ),
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
            i32ConstInstruction(),
            jumpIfNotInstruction(
                offset = 3,
            ),
            i32ConstInstruction(),
            i32ConstInstruction(),
            localSetInstruction(),
            returnFunctionInstruction(),
        )

        val actual = ControlFlowPass(context, module).functions[0].body.instructions
        assertEquals(expected, actual)
    }

    @Test
    fun `can unnest an if else expression into a flat list of instructions`() {

        val instructions = listOf(
            i32ConstInstruction(),
            ifInstruction(
                blockType = valueBlockType(i32ValueType()),
                thenInstructions = listOf(
                    i32ConstInstruction(),
                ),
                elseInstructions = listOf(
                    i32ConstInstruction(),
                ),
            ),
            localSetInstruction(),
        )
        val module = module(
            definedTypes = listOf(
                functionType().definedType(),
            ),
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
            i32ConstInstruction(),
            jumpIfNotInstruction(
                offset = 3,
            ),
            i32ConstInstruction(),
            jumpInstruction(
                offset = 2,
            ),
            i32ConstInstruction(),
            localSetInstruction(),
            returnFunctionInstruction(),
        )

        val actual = ControlFlowPass(context, module).functions[0].body.instructions
        assertEquals(expected, actual)
    }

    @Test
    fun `can unnest a loop expression into a flat list of instructions`() {

        val instructions = listOf(
            i32ConstInstruction(),
            i32ConstInstruction(),
            loopInstruction(
                blockType = signedTypeIndexBlockType(1),
                instructions = listOf(
                    i32ConstInstruction(),
                    i32ConstInstruction(),
                    brIfInstruction(labelIndex(0)),
                ),
            ),
            localSetInstruction(),
        )
        val module = module(
            definedTypes = listOf(
                functionType().definedType(),
                functionType(
                    resultType(
                        listOf(
                            i32ValueType(),
                            i32ValueType(),
                        ),
                    ),
                    resultType(
                        listOf(
                            i32ValueType(),
                        ),
                    ),
                ).definedType(),
            ),
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
            i32ConstInstruction(),
            i32ConstInstruction(),
            i32ConstInstruction(),
            i32ConstInstruction(),
            jumpIfInstruction(
                offset = -2,
                adjustment = stackAdjustment(
                    depth = 0,
                    keep = 2,
                ),
            ),
            localSetInstruction(),
            returnFunctionInstruction(),
        )

        val actual = ControlFlowPass(context, module).functions[0].body.instructions
        assertEquals(expected, actual)
    }

    @Test
    fun `can break from a backwards block into a forwards block into a flat list of instructions`() {

        val instructions = listOf(
            i32ConstInstruction(),
            blockInstruction(
                blockType = valueBlockType(i32ValueType()),
                instructions = listOf(
                    i32ConstInstruction(),
                    loopInstruction(
                        blockType = signedTypeIndexBlockType(1),
                        instructions = listOf(
                            i32ConstInstruction(),
                            i32ConstInstruction(),
                            brIfInstruction(labelIndex(1)),
                        ),
                    ),
                    i32ConstInstruction(),
                ),
            ),
            localSetInstruction(),
        )
        val module = module(
            definedTypes = listOf(
                functionType().definedType(),
                functionType(
                    resultType(
                        listOf(
                            i32ValueType(),
                            i32ValueType(),
                        ),
                    ),
                    resultType(
                        listOf(
                            i32ValueType(),
                        ),
                    ),
                ).definedType(),
            ),
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
            i32ConstInstruction(),
            i32ConstInstruction(),
            i32ConstInstruction(),
            i32ConstInstruction(),
            jumpIfInstruction(
                offset = 2,
                adjustment = stackAdjustment(
                    depth = 1,
                    keep = 1,
                ),
            ),
            i32ConstInstruction(),
            localSetInstruction(),
            returnFunctionInstruction(),
        )

        val actual = ControlFlowPass(context, module).functions[0].body.instructions
        assertEquals(expected, actual)
    }

    @Test
    fun `can break from a forwards block into a backwards block into a flat list of instructions`() {

        val instructions = listOf(
            i32ConstInstruction(),
            i32ConstInstruction(),
            loopInstruction(
                blockType = signedTypeIndexBlockType(1),
                instructions = listOf(
                    i32ConstInstruction(),
                    i32ConstInstruction(),
                    blockInstruction(
                        blockType = valueBlockType(i32ValueType()),
                        instructions = listOf(
                            i32ConstInstruction(),
                            brInstruction(labelIndex(1)),
                        ),
                    ),
                ),
            ),
            localSetInstruction(),
        )
        val module = module(
            definedTypes = listOf(
                functionType().definedType(),
                functionType(
                    resultType(
                        listOf(
                            i32ValueType(),
                            i32ValueType(),
                        ),
                    ),
                    resultType(
                        listOf(
                            i32ValueType(),
                        ),
                    ),
                ).definedType(),
            ),
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
            i32ConstInstruction(),
            i32ConstInstruction(),
            i32ConstInstruction(),
            i32ConstInstruction(),
            i32ConstInstruction(),
            jumpAdjustingInstruction(
                offset = -3,
                adjustment = stackAdjustment(
                    depth = 0,
                    keep = 2,
                ),
            ),
            localSetInstruction(),
            returnFunctionInstruction(),
        )

        val actual = ControlFlowPass(context, module).functions[0].body.instructions
        assertEquals(expected, actual)
    }
}
