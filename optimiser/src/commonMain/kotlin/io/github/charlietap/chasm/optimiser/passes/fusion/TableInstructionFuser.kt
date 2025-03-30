package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.optimiser.passes.PassContextt

internal typealias TableInstructionFuser = (PassContextt, Int, TableInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun TableInstructionFuser(
    context: PassContextt,
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
    context: PassContextt,
    index: Int,
    instruction: TableInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
): Int = when (instruction) {
    is TableInstruction.TableGet -> {

        var nextIndex = index
        val elementIndex = input.getOrNull(index - 1)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (elementIndex == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                elementIndex == null -> FusedTableInstruction.TableGet(
                    elementIndex = FusedOperand.ValueStack,
                    destination = destination,
                    tableIdx = instruction.tableIdx,
                )
                else -> {
                    output.removeLast()
                    FusedTableInstruction.TableGet(
                        elementIndex = elementIndex,
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
    is TableInstruction.TableSet -> {

        val value = input.getOrNull(index - 1)?.let(operandFactory)
        val elementIdx = input.getOrNull(index - 2)?.let(operandFactory)

        val instruction = if (value == null) {
            instruction
        } else {
            when {
                elementIdx == null -> {
                    output.removeLast()
                    FusedTableInstruction.TableSet(
                        value = value,
                        elementIdx = FusedOperand.ValueStack,
                        tableIdx = instruction.tableIdx,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    FusedTableInstruction.TableSet(
                        value = value,
                        elementIdx = elementIdx,
                        tableIdx = instruction.tableIdx,
                    )
                }
            }
        }

        output.add(instruction)
        index
    }
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
    is TableInstruction.TableInit -> {

        val elementsToInit = input.getOrNull(index - 1)?.let(operandFactory)
        val segmentOffset = input.getOrNull(index - 2)?.let(operandFactory)
        val tableOffset = input.getOrNull(index - 3)?.let(operandFactory)

        val instruction = if (elementsToInit == null) {
            instruction
        } else {
            when {
                segmentOffset == null -> {
                    output.removeLast()
                    FusedTableInstruction.TableInit(
                        elementsToInitialise = elementsToInit,
                        segmentOffset = FusedOperand.ValueStack,
                        tableOffset = FusedOperand.ValueStack,
                        tableIdx = instruction.tableIdx,
                        elemIdx = instruction.elemIdx,
                    )
                }
                tableOffset == null -> {
                    output.removeLast()
                    output.removeLast()
                    FusedTableInstruction.TableInit(
                        elementsToInitialise = elementsToInit,
                        segmentOffset = segmentOffset,
                        tableOffset = FusedOperand.ValueStack,
                        tableIdx = instruction.tableIdx,
                        elemIdx = instruction.elemIdx,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    FusedTableInstruction.TableInit(
                        elementsToInitialise = elementsToInit,
                        segmentOffset = segmentOffset,
                        tableOffset = tableOffset,
                        tableIdx = instruction.tableIdx,
                        elemIdx = instruction.elemIdx,
                    )
                }
            }
        }

        output.add(instruction)
        index
    }
    is TableInstruction.TableSize -> {
        var nextIndex = index
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (destination == FusedDestination.ValueStack) {
            instruction
        } else {
            FusedTableInstruction.TableSize(
                destination = destination,
                tableIdx = instruction.tableIdx,
            )
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
