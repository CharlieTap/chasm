package io.github.charlietap.chasm.compiler.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction

internal typealias BinopFusedInstructionFactory = (FusedOperand, FusedOperand, FusedDestination) -> Instruction
internal typealias BinopFuser = (Int, Instruction, List<Instruction>, MutableList<Instruction>, BinopFusedInstructionFactory) -> Int

/**
 * When we load two operands for a binary numeric operation we avoid
 * storing them in temporary variables and place them directly into
 * the numeric expression i.e.
 *
 * load_left operation load_right
 *
 * This wouldn't work if they both pull from the stack as right needs
 * to be popped first and thus right would be in the left position. For
 * this reason this specialised fuser exists which omits fusions where
 * both operands are missing but the destination is present.
 */
internal fun NonCommutativeBinopFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: BinopFusedInstructionFactory,
): Int = NonCommutativeBinopFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    fusedInstructionFactory = fusedInstructionFactory,
    operandFactory = ::FusedOperandFactory,
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun NonCommutativeBinopFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: BinopFusedInstructionFactory,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
): Int {

    var nextIndex = index

    val right = input.getOrNull(index - 1)?.let(operandFactory)
    val left = input.getOrNull(index - 2)?.let(operandFactory)
    val destination = input.getOrNull(index + 1).let(destinationFactory)

    val instruction = when {
        right == null -> instruction
        left == null -> {
            output.removeLast()
            fusedInstructionFactory(FusedOperand.ValueStack, right, destination)
        }
        else -> {
            output.removeLast()
            output.removeLast()
            fusedInstructionFactory(left, right, destination)
        }
    }

    output.add(instruction)

    if (right != null && destination != FusedDestination.ValueStack) {
        nextIndex++
    }

    return nextIndex
}
