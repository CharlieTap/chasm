package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.Expression as IRExpression
import io.github.charlietap.chasm.ir.instruction.Instruction as IRInstruction

internal fun ExpressionFactory(
    expression: Expression,
) = ExpressionFactory(
    expression = expression,
    instructionFactory = ::InstructionFactory,
)

internal inline fun ExpressionFactory(
    expression: Expression,
    instructionFactory: IRFactory<Instruction, IRInstruction>,
) = IRExpression(
    instructions = expression.instructions.map(instructionFactory),
)
