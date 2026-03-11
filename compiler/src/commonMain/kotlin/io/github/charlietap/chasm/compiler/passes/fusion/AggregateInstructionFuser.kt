package io.github.charlietap.chasm.compiler.passes.fusion

import io.github.charlietap.chasm.compiler.passes.PassContext
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction

internal typealias AggregateInstructionFuser = (PassContext, Int, AggregateInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun AggregateInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: AggregateInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = AggregateInstructionFuser(
    context = context,
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    operandFactory = ::FusedOperandFactory,
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun AggregateInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: AggregateInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
): Int = when (instruction) {
    is AggregateInstruction.ArrayCopy -> {

        val elementsToCopy = input.getOrNull(index - 1)?.let(operandFactory)
        val sourceOffset = input.getOrNull(index - 2)?.let(operandFactory)
        val sourceAddress = input.getOrNull(index - 3)?.let(operandFactory)
        val destinationOffset = input.getOrNull(index - 4)?.let(operandFactory)
        val destinationAddress = input.getOrNull(index - 5)?.let(operandFactory)

        val instruction = if (elementsToCopy == null) {
            instruction
        } else {
            when {
                sourceOffset == null -> {
                    output.removeLast()
                    AggregateSuperInstruction.ArrayCopy(
                        elementsToCopy = elementsToCopy,
                        sourceOffset = FusedOperand.ValueStack,
                        sourceAddress = FusedOperand.ValueStack,
                        destinationOffset = FusedOperand.ValueStack,
                        destinationAddress = FusedOperand.ValueStack,
                        sourceTypeIndex = instruction.sourceTypeIndex,
                        destinationTypeIndex = instruction.destinationTypeIndex,
                    )
                }
                sourceAddress == null -> {
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayCopy(
                        elementsToCopy = elementsToCopy,
                        sourceOffset = sourceOffset,
                        sourceAddress = FusedOperand.ValueStack,
                        destinationOffset = FusedOperand.ValueStack,
                        destinationAddress = FusedOperand.ValueStack,
                        sourceTypeIndex = instruction.sourceTypeIndex,
                        destinationTypeIndex = instruction.destinationTypeIndex,
                    )
                }
                destinationOffset == null -> {
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayCopy(
                        elementsToCopy = elementsToCopy,
                        sourceOffset = sourceOffset,
                        sourceAddress = sourceAddress,
                        destinationOffset = FusedOperand.ValueStack,
                        destinationAddress = FusedOperand.ValueStack,
                        sourceTypeIndex = instruction.sourceTypeIndex,
                        destinationTypeIndex = instruction.destinationTypeIndex,
                    )
                }
                destinationAddress == null -> {
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayCopy(
                        elementsToCopy = elementsToCopy,
                        sourceOffset = sourceOffset,
                        sourceAddress = sourceAddress,
                        destinationOffset = destinationOffset,
                        destinationAddress = FusedOperand.ValueStack,
                        sourceTypeIndex = instruction.sourceTypeIndex,
                        destinationTypeIndex = instruction.destinationTypeIndex,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayCopy(
                        elementsToCopy = elementsToCopy,
                        sourceOffset = sourceOffset,
                        sourceAddress = sourceAddress,
                        destinationOffset = destinationOffset,
                        destinationAddress = destinationAddress,
                        sourceTypeIndex = instruction.sourceTypeIndex,
                        destinationTypeIndex = instruction.destinationTypeIndex,
                    )
                }
            }
        }

        output.add(instruction)

        index
    }
    is AggregateInstruction.ArrayFill -> {

        val elementsToFill = input.getOrNull(index - 1)?.let(operandFactory)
        val fillValue = input.getOrNull(index - 2)?.let(operandFactory)
        val arrayElementOffset = input.getOrNull(index - 3)?.let(operandFactory)
        val address = input.getOrNull(index - 4)?.let(operandFactory)

        val instruction = if (elementsToFill == null) {
            instruction
        } else {
            when {
                fillValue == null -> {
                    output.removeLast()
                    AggregateSuperInstruction.ArrayFill(
                        elementsToFill = elementsToFill,
                        fillValue = FusedOperand.ValueStack,
                        arrayElementOffset = FusedOperand.ValueStack,
                        address = FusedOperand.ValueStack,
                        typeIndex = instruction.typeIndex,
                    )
                }
                arrayElementOffset == null -> {
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayFill(
                        elementsToFill = elementsToFill,
                        fillValue = fillValue,
                        arrayElementOffset = FusedOperand.ValueStack,
                        address = FusedOperand.ValueStack,
                        typeIndex = instruction.typeIndex,
                    )
                }
                address == null -> {
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayFill(
                        elementsToFill = elementsToFill,
                        fillValue = fillValue,
                        arrayElementOffset = arrayElementOffset,
                        address = FusedOperand.ValueStack,
                        typeIndex = instruction.typeIndex,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayFill(
                        elementsToFill = elementsToFill,
                        fillValue = fillValue,
                        arrayElementOffset = arrayElementOffset,
                        address = address,
                        typeIndex = instruction.typeIndex,
                    )
                }
            }
        }

        output.add(instruction)

        index
    }
    is AggregateInstruction.ArrayGet -> {
        var nextIndex = index

        val field = input.getOrNull(index - 1)?.let(operandFactory)
        val address = input.getOrNull(index - 2)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (field == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                field == null -> AggregateSuperInstruction.ArrayGet(
                    field = FusedOperand.ValueStack,
                    address = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                )
                address == null -> {
                    output.removeLast()
                    AggregateSuperInstruction.ArrayGet(
                        field = field,
                        address = FusedOperand.ValueStack,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayGet(
                        field = field,
                        address = address,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
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
    is AggregateInstruction.ArrayGetSigned -> {
        var nextIndex = index

        val field = input.getOrNull(index - 1)?.let(operandFactory)
        val address = input.getOrNull(index - 2)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (field == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                field == null -> AggregateSuperInstruction.ArrayGetSigned(
                    field = FusedOperand.ValueStack,
                    address = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                )
                address == null -> {
                    output.removeLast()
                    AggregateSuperInstruction.ArrayGetSigned(
                        field = field,
                        address = FusedOperand.ValueStack,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayGetSigned(
                        field = field,
                        address = address,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
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
    is AggregateInstruction.ArrayGetUnsigned -> {
        var nextIndex = index

        val field = input.getOrNull(index - 1)?.let(operandFactory)
        val address = input.getOrNull(index - 2)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (field == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                field == null -> AggregateSuperInstruction.ArrayGetUnsigned(
                    field = FusedOperand.ValueStack,
                    address = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                )
                address == null -> {
                    output.removeLast()
                    AggregateSuperInstruction.ArrayGetUnsigned(
                        field = field,
                        address = FusedOperand.ValueStack,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayGetUnsigned(
                        field = field,
                        address = address,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
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
    is AggregateInstruction.ArrayLen -> {
        var nextIndex = index

        val address = input.getOrNull(index - 1)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (address == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                address == null -> AggregateSuperInstruction.ArrayLen(
                    address = FusedOperand.ValueStack,
                    destination = destination,
                )
                else -> {
                    output.removeLast()
                    AggregateSuperInstruction.ArrayLen(
                        address = address,
                        destination = destination,
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
    is AggregateInstruction.ArrayNew -> {

        var nextIndex = index

        val size = input.getOrNull(index - 1)?.let(operandFactory)
        val value = input.getOrNull(index - 2)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (size == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                size == null -> AggregateSuperInstruction.ArrayNew(
                    size = FusedOperand.ValueStack,
                    value = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                )
                value == null -> {
                    output.removeLast()
                    AggregateSuperInstruction.ArrayNew(
                        size = size,
                        value = FusedOperand.ValueStack,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArrayNew(
                        size = size,
                        value = value,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
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
    is AggregateInstruction.ArrayNewFixed -> {

        var nextIndex = index

        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (destination == FusedDestination.ValueStack) {
            instruction
        } else {
            AggregateSuperInstruction.ArrayNewFixed(
                destination = destination,
                typeIndex = instruction.typeIndex,
                size = instruction.size.toInt(),
            )
        }

        output.add(instruction)

        if (destination != FusedDestination.ValueStack) {
            nextIndex++
        }

        nextIndex
    }
    is AggregateInstruction.ArraySet -> {

        val value = input.getOrNull(index - 1)?.let(operandFactory)
        val field = input.getOrNull(index - 2)?.let(operandFactory)
        val address = input.getOrNull(index - 3)?.let(operandFactory)

        val instruction = if (value == null) {
            instruction
        } else {
            when {
                field == null -> {
                    output.removeLast()
                    AggregateSuperInstruction.ArraySet(
                        value = value,
                        field = FusedOperand.ValueStack,
                        address = FusedOperand.ValueStack,
                        typeIndex = instruction.typeIndex,
                    )
                }
                address == null -> {
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArraySet(
                        value = value,
                        field = field,
                        address = FusedOperand.ValueStack,
                        typeIndex = instruction.typeIndex,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.ArraySet(
                        value = value,
                        field = field,
                        address = address,
                        typeIndex = instruction.typeIndex,
                    )
                }
            }
        }

        output.add(instruction)

        index
    }
    is AggregateInstruction.StructGet -> {
        var nextIndex = index

        val address = input.getOrNull(index - 1)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (address == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                address == null -> AggregateSuperInstruction.StructGet(
                    address = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                    fieldIndex = instruction.fieldIndex,
                )
                else -> {
                    output.removeLast()
                    AggregateSuperInstruction.StructGet(
                        address = address,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
                        fieldIndex = instruction.fieldIndex,
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
    is AggregateInstruction.StructGetSigned -> {
        var nextIndex = index

        val address = input.getOrNull(index - 1)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (address == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                address == null -> AggregateSuperInstruction.StructGetSigned(
                    address = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                    fieldIndex = instruction.fieldIndex,
                )
                else -> {
                    output.removeLast()
                    AggregateSuperInstruction.StructGetSigned(
                        address = address,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
                        fieldIndex = instruction.fieldIndex,
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
    is AggregateInstruction.StructGetUnsigned -> {
        var nextIndex = index

        val address = input.getOrNull(index - 1)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (address == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                address == null -> AggregateSuperInstruction.StructGetUnsigned(
                    address = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                    fieldIndex = instruction.fieldIndex,
                )
                else -> {
                    output.removeLast()
                    AggregateSuperInstruction.StructGetUnsigned(
                        address = address,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
                        fieldIndex = instruction.fieldIndex,
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
    is AggregateInstruction.StructNew -> {
        var nextIndex = index

        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (destination == FusedDestination.ValueStack) {
            instruction
        } else {
            AggregateSuperInstruction.StructNew(
                destination = destination,
                typeIndex = instruction.typeIndex,
            )
        }

        output.add(instruction)

        if (destination != FusedDestination.ValueStack) {
            nextIndex++
        }

        nextIndex
    }
    is AggregateInstruction.StructNewDefault -> {
        var nextIndex = index

        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (destination == FusedDestination.ValueStack) {
            instruction
        } else {
            AggregateSuperInstruction.StructNewDefault(
                destination = destination,
                typeIndex = instruction.typeIndex,
            )
        }

        output.add(instruction)

        if (destination != FusedDestination.ValueStack) {
            nextIndex++
        }

        nextIndex
    }
    is AggregateInstruction.StructSet -> {

        val value = input.getOrNull(index - 1)?.let(operandFactory)
        val address = input.getOrNull(index - 2)?.let(operandFactory)

        val instruction = if (value == null) {
            instruction
        } else {
            when {
                address == null -> {
                    output.removeLast()
                    AggregateSuperInstruction.StructSet(
                        value = value,
                        address = FusedOperand.ValueStack,
                        typeIndex = instruction.typeIndex,
                        fieldIndex = instruction.fieldIndex,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    AggregateSuperInstruction.StructSet(
                        value = value,
                        address = address,
                        typeIndex = instruction.typeIndex,
                        fieldIndex = instruction.fieldIndex,
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
