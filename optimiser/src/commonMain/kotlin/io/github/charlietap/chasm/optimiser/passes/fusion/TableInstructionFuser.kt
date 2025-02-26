package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
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
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun TableInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: TableInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
): Int = when (instruction) {
    is TableInstruction.TableCopy -> {

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
    is TableInstruction.TableFill -> {

        val elementsToFill = input.getOrNull(index - 1)?.let(operandFactory)
        val fillValue = input.getOrNull(index - 2)?.let(operandFactory)
        val tableOffset = input.getOrNull(index - 3)?.let(operandFactory)

        val instruction = if (elementsToFill == null) {
            instruction
        } else {
            when {
                fillValue == null -> {
                    output.removeLast()
                    FusedTableInstruction.TableFill(
                        elementsToFill = elementsToFill,
                        fillValue = FusedOperand.ValueStack,
                        tableOffset = FusedOperand.ValueStack,
                        tableIdx = instruction.tableIdx,
                    )
                }
                tableOffset == null -> {
                    output.removeLast()
                    output.removeLast()
                    FusedTableInstruction.TableFill(
                        elementsToFill = elementsToFill,
                        fillValue = fillValue,
                        tableOffset = FusedOperand.ValueStack,
                        tableIdx = instruction.tableIdx,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    FusedTableInstruction.TableFill(
                        elementsToFill = elementsToFill,
                        fillValue = fillValue,
                        tableOffset = tableOffset,
                        tableIdx = instruction.tableIdx,
                    )
                }
            }
        }

        output.add(instruction)
        index
    }
    is TableInstruction.TableGrow -> {

        var nextIndex = index
        val elementsToAdd = input.getOrNull(index - 1)?.let(operandFactory)
        val referenceValue = input.getOrNull(index - 2)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (elementsToAdd == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                elementsToAdd == null -> FusedTableInstruction.TableGrow(
                    elementsToAdd = FusedOperand.ValueStack,
                    referenceValue = FusedOperand.ValueStack,
                    destination = destination,
                    tableIdx = instruction.tableIdx,
                )
                referenceValue == null -> {
                    output.removeLast()
                    FusedTableInstruction.TableGrow(
                        elementsToAdd = elementsToAdd,
                        referenceValue = FusedOperand.ValueStack,
                        destination = destination,
                        tableIdx = instruction.tableIdx,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    FusedTableInstruction.TableGrow(
                        elementsToAdd = elementsToAdd,
                        referenceValue = referenceValue,
                        destination = destination,
                        tableIdx = instruction.tableIdx,
                    )
                }
            }
        }

        output.add(instruction)

        if (destination != FusedDestination.ValueStack) {
            nextIndex++
        }

        nextIndex
    }
    else -> {
        output.add(instruction)
        index
    }
}
