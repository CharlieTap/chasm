package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.type.BlockType
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.ir.instruction.ControlInstruction as IRControlInstruction
import io.github.charlietap.chasm.ir.module.Index as IRIndex

class ControlInstructionFactoryTest {

    @Test
    fun `maps structured control as a flat instruction sequence`() {
        val expression = Expression(
            ControlInstruction.Block(BlockType.Empty),
            ControlInstruction.Loop(BlockType.Empty),
            ControlInstruction.If(BlockType.Empty),
            ControlInstruction.TryTable(
                blockType = BlockType.Empty,
                handlers = listOf(
                    ControlInstruction.CatchHandler.CatchAll(
                        labelIndex = Index.LabelIndex(2u),
                    ),
                ),
            ),
            ControlInstruction.Else,
            ControlInstruction.End(4),
        )

        val actual = ExpressionFactory(expression)

        assertEquals(
            listOf(
                IRControlInstruction.Block(BlockType.Empty),
                IRControlInstruction.Loop(BlockType.Empty),
                IRControlInstruction.If(BlockType.Empty),
                IRControlInstruction.TryTable(
                    blockType = BlockType.Empty,
                    handlers = listOf(
                        IRControlInstruction.CatchHandler.CatchAll(
                            labelIndex = IRIndex.LabelIndex(2),
                        ),
                    ),
                    payloadDestinationSlots = emptyList(),
                ),
                IRControlInstruction.Else,
                IRControlInstruction.End(4),
            ),
            actual.instructions,
        )
    }

    @Test
    fun `maps deeply nested control without using the host stack`() {
        val depth = 20_000
        val expression = Expression(
            instructions = buildList {
                repeat(depth) {
                    add(ControlInstruction.Block(BlockType.Empty))
                }
                add(ControlInstruction.End(depth))
            },
        )

        val actual = ExpressionFactory(expression)

        assertEquals(depth + 1, actual.instructions.size)
        assertEquals(IRControlInstruction.Block(BlockType.Empty), actual.instructions.first())
        assertEquals(IRControlInstruction.End(depth), actual.instructions.last())
    }
}
