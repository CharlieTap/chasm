package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.TableInstruction

internal typealias TableInstructionFuser = (PassContext, Int, TableInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun TableInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: TableInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = TableInstructionFuser(
    context = context,
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    operandFactory = ::FusedOperandFactory,
)

internal inline fun TableInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: TableInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    operandFactory: FusedOperandFactory,
): Int = when (instruction) {
    is TableInstruction.TableCopy,
    -> {
        val elementsToCopy = input.getOrNull(index - 1)?.let(operandFactory)
        val srcOffset = input.getOrNull(index - 2)?.let(operandFactory)
        val dstOffset = input.getOrNull(index - 3)?.let(operandFactory)

        val instruction = if (elementsToCopy == null) {
            instruction
        } else {
            when {
                srcOffset == null -> {
                    output.removeLast()
                    FusedTableInstruction.TableCopy(
                        elementsToCopy = elementsToCopy,
                        srcOffset = FusedOperand.ValueStack,
                        dstOffset = FusedOperand.ValueStack,
                        srcTableIdx = instruction.srcTableIdx,
                        destTableIdx = instruction.destTableIdx,
                    )
                }
                dstOffset == null -> {
                    output.removeLast()
                    output.removeLast()
                    FusedTableInstruction.TableCopy(
                        elementsToCopy = elementsToCopy,
                        srcOffset = srcOffset,
                        dstOffset = FusedOperand.ValueStack,
                        srcTableIdx = instruction.srcTableIdx,
                        destTableIdx = instruction.destTableIdx,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    FusedTableInstruction.TableCopy(
                        elementsToCopy = elementsToCopy,
                        srcOffset = srcOffset,
                        dstOffset = dstOffset,
                        srcTableIdx = instruction.srcTableIdx,
                        destTableIdx = instruction.destTableIdx,
                    )
                }
            }
        }

        output.add(instruction)
        index
    }
    else -> {
        output.add(instruction)
        index
    }
}
