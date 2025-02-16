package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction

internal typealias ExpressionFuser = (Expression) -> Expression

internal fun ExpressionFuser(
    expression: Expression,
): Expression =
    ExpressionFuser(
        expression = expression,
        fuser = ::InstructionFuser,
    )

internal inline fun ExpressionFuser(
    expression: Expression,
    fuser: InstructionFuser,
): Expression {
    val instructions = expression.instructions
    val fused = mutableListOf<Instruction>()

    var idx = 0
    while (idx < instructions.size) {

        val instruction = instructions[idx]
        idx = fuser(idx, instruction, instructions, fused)

        idx++
    }

    return Expression(fused)
}
