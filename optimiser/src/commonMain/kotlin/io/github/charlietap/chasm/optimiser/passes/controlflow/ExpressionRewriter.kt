package io.github.charlietap.chasm.optimiser.passes.controlflow

import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.optimiser.passes.PassContext

internal typealias ExpressionRewriter = (PassContext, Expression) -> Expression

internal fun ExpressionRewriter(
    context: PassContext,
    expression: Expression,
): Expression = ExpressionRewriter(
    context = context,
    expression = expression,
    instructionRewriter = ::InstructionRewriter,
)

internal inline fun ExpressionRewriter(
    context: PassContext,
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
