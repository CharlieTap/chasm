package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction

internal typealias ControlInstructionFuser = (Int, ControlInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun ControlInstructionFuser(
    index: Int,
    instruction: ControlInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = ControlInstructionFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    expressionFuser = ::ExpressionFuser,
    unop = ::UnopFuser,
    binop = ::BinopFuser,
)

internal inline fun ControlInstructionFuser(
    index: Int,
    instruction: ControlInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    expressionFuser: ExpressionFuser,
    unop: UnopFuser,
    binop: BinopFuser,
): Int = when (instruction) {
    is ControlInstruction.Block -> {

        val expression = Expression(instruction.instructions)
        val fusedExpression = expressionFuser(expression)

        output.add(
            instruction.copy(
                instructions = fusedExpression.instructions,
            ),
        )
        index
    }
    is ControlInstruction.If -> {

        val thenExpression = Expression(instruction.thenInstructions)
        val fusedThenExpression = expressionFuser(thenExpression)

        val fusedElseExpression = instruction.elseInstructions?.let {
            expressionFuser(Expression(it))
        }

        output.add(
            instruction.copy(
                thenInstructions = fusedThenExpression.instructions,
                elseInstructions = fusedElseExpression?.instructions,
            ),
        )
        index
    }
    is ControlInstruction.Loop -> {

        val expression = Expression(instruction.instructions)
        val fusedExpression = expressionFuser(expression)

        output.add(
            instruction.copy(
                instructions = fusedExpression.instructions,
            ),
        )
        index
    }
    is ControlInstruction.TryTable -> {

        val expression = Expression(instruction.instructions)
        val fusedExpression = expressionFuser(expression)

        output.add(
            instruction.copy(
                instructions = fusedExpression.instructions,
            ),
        )
        index
    }
    else -> {
        output.add(instruction)
        index
    }
}
