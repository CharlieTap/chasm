package io.github.charlietap.chasm.compiler.passes.fusion

import io.github.charlietap.chasm.compiler.passes.PassContext
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.type.ext.functionType

internal typealias ControlInstructionFuser = (PassContext, Int, ControlInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun ControlInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: ControlInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = ControlInstructionFuser(
    context = context,
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    operandFactory = ::FusedOperandFactory,
)

internal inline fun ControlInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: ControlInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    operandFactory: FusedOperandFactory,
): Int = when (instruction) {
    is ControlInstruction.Block,
    is ControlInstruction.Loop,
    is ControlInstruction.TryTable,
    ControlInstruction.Else,
    is ControlInstruction.End,
    -> {
        output.add(instruction)
        index
    }
    is ControlInstruction.BrIf -> {

        val operand = input.getOrNull(index - 1)?.let(operandFactory)

        if (operand == null) {
            output.add(instruction)
        } else {
            output.removeLast()
            output.add(
                ControlSuperInstruction.BrIf(
                    operand = operand,
                    labelIndex = instruction.labelIndex,
                ),
            )
        }

        index
    }
    is ControlInstruction.If -> {
        val operand = input.getOrNull(index - 1)?.let(operandFactory)

        if (operand == null) {
            output.add(instruction)
        } else {
            output.removeLast()
            output.add(
                ControlSuperInstruction.If(
                    operand = operand,
                    blockType = instruction.blockType,
                ),
            )
        }

        index
    }
    // TODO Implement logic to fuse operands that do not immediately precede the call instructions
    is ControlInstruction.Call -> {

        val type = context.functionTypes[instruction.functionIndex.idx].functionType()

        if (type == null) {
            output.add(instruction)
        } else {

            val operands = List(type.params.types.size) { idx ->
                val opidx = index - (idx + 1)
                input.getOrNull(opidx)?.let(operandFactory)
            }.asReversed()

            if (operands.all { it != null }) {
                repeat(type.params.types.size) { output.removeLast() }
                output.add(
                    ControlSuperInstruction.Call(
                        operands = operands.map { it ?: FusedOperand.ValueStack },
                        functionIndex = instruction.functionIndex,
                    ),
                )
            } else {
                output.add(instruction)
            }
        }

        index
    }
    else -> {
        output.add(instruction)
        index
    }
}
