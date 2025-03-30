package io.github.charlietap.chasm.optimiser.passes.controlflow

import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction

internal typealias ExpressionRewriter = (ControlFlowContext, Expression) -> Expression

internal fun ExpressionRewriter(
    context: ControlFlowContext,
    expression: Expression,
): Expression = ExpressionRewriter(
    context = context,
    expression = expression,
    instructionRewriter = ::InstructionRewriter,
)

internal inline fun ExpressionRewriter(
    context: ControlFlowContext,
    expression: Expression,
    instructionRewriter: InstructionRewriter,
): Expression {

    val instructions = mutableListOf<Instruction>()
    for (instruction in expression.instructions) {
        instructionRewriter(context, instruction, instructions)
    }

    return Expression(
        instructions = instructions,
    )
}
