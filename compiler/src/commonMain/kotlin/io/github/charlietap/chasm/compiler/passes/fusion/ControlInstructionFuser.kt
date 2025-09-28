package io.github.charlietap.chasm.compiler.passes.fusion

import io.github.charlietap.chasm.compiler.passes.PassContext
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction
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
    expressionFuser = ::ExpressionFuser,
    operandFactory = ::FusedOperandFactory,
)

internal inline fun ControlInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: ControlInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    expressionFuser: ExpressionFuser,
    operandFactory: FusedOperandFactory,
): Int = when (instruction) {
    is ControlInstruction.Block -> {

        val expression = Expression(instruction.instructions)
        val fusedExpression = expressionFuser(context, expression)

        output.add(
            instruction.copy(
                instructions = fusedExpression.instructions,
            ),
        )
        index
    }
    is ControlInstruction.BrIf -> {

        val operand = input.getOrNull(index - 1)?.let(operandFactory)

        if (operand == null) {
            output.add(instruction)
        } else {
            output.removeLast()
            output.add(
                FusedControlInstruction.BrIf(
                    operand = operand,
                    labelIndex = instruction.labelIndex,
                ),
            )
        }

        index
    }
    is ControlInstruction.If -> {

        val thenExpression = Expression(instruction.thenInstructions)
        val fusedThenExpression = expressionFuser(context, thenExpression)

        val fusedElseExpression = instruction.elseInstructions?.let {
            expressionFuser(context, Expression(it))
        }

        val operand = input.getOrNull(index - 1)?.let(operandFactory)

        if (operand == null) {
            output.add(
                instruction.copy(
                    thenInstructions = fusedThenExpression.instructions,
                    elseInstructions = fusedElseExpression?.instructions,
                ),
            )
        } else {
            output.removeLast()
            output.add(
                FusedControlInstruction.If(
                    operand = operand,
                    blockType = instruction.blockType,
                    thenInstructions = fusedThenExpression.instructions,
                    elseInstructions = fusedElseExpression?.instructions,
                ),
            )
        }

        index
    }
    is ControlInstruction.Loop -> {

        val expression = Expression(instruction.instructions)
        val fusedExpression = expressionFuser(context, expression)

        output.add(
            instruction.copy(
                instructions = fusedExpression.instructions,
            ),
        )
        index
    }
    is ControlInstruction.TryTable -> {

        val expression = Expression(instruction.instructions)
        val fusedExpression = expressionFuser(context, expression)

        output.add(
            instruction.copy(
                instructions = fusedExpression.instructions,
            ),
        )
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
                    FusedControlInstruction.Call(
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
