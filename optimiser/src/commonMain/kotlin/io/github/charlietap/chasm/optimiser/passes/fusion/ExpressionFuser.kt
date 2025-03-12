package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.optimiser.passes.PassContext

internal typealias ExpressionFuser = (PassContext, Expression) -> Expression

internal fun ExpressionFuser(
    context: PassContext,
    expression: Expression,
): Expression =
    ExpressionFuser(
        context = context,
        expression = expression,
        fuser = ::InstructionFuser,
    )

internal inline fun ExpressionFuser(
    context: PassContext,
    expression: Expression,
    fuser: InstructionFuser,
): Expression {
    val instructions = expression.instructions
    val fused = mutableListOf<Instruction>()

    var idx = 0
    while (idx < instructions.size) {

        val instruction = instructions[idx]
        idx = fuser(context, idx, instruction, instructions, fused)

        idx++
    }

    return Expression(fused)
}
