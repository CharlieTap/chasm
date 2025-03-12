package io.github.charlietap.chasm.optimiser.passes.controlflow

import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.optimiser.passes.PassContext

internal typealias InstructionRewriter = (PassContext, Instruction, MutableList<Instruction>) -> Unit

internal fun InstructionRewriter(
    context: PassContext,
    instruction: Instruction,
    output: MutableList<Instruction>,
) = InstructionRewriter(
    context = context,
    instruction = instruction,
    output = output,
    expressionRewriter = ::ExpressionRewriter,
)

internal inline fun InstructionRewriter(
    context: PassContext,
    instruction: Instruction,
    output: MutableList<Instruction>,
    expressionRewriter: ExpressionRewriter,
) {
    when (instruction) {
        is ControlInstruction.Block -> {

            val expression = expressionRewriter(context, Expression(instruction.instructions))

            val rewritten = instruction.copy(
                instructions = buildList {
                    addAll(expression.instructions)
                    add(AdminInstruction.EndBlock)
                },
            )
            output.add(rewritten)
        }
        is ControlInstruction.If -> {

            val expression = expressionRewriter(context, Expression(instruction.thenInstructions))

            val rewritten = instruction.copy(
                thenInstructions = buildList {
                    addAll(expression.instructions)
                    add(AdminInstruction.EndBlock)
                },
                elseInstructions = buildList {
                    instruction.elseInstructions?.let {
                        val elseExpression = expressionRewriter(context, Expression(it))
                        addAll(elseExpression.instructions)
                    }
                    add(AdminInstruction.EndBlock)
                },
            )
            output.add(rewritten)
        }
        is ControlInstruction.Loop -> {
            val expression = expressionRewriter(context, Expression(instruction.instructions))

            val rewritten = instruction.copy(
                instructions = buildList {
                    addAll(expression.instructions)
                    add(AdminInstruction.EndBlock)
                },
            )
            output.add(rewritten)
        }
        is ControlInstruction.TryTable -> {
            val expression = expressionRewriter(context, Expression(instruction.instructions))

            val rewritten = instruction.copy(
                instructions = buildList {
                    addAll(expression.instructions)
                    add(AdminInstruction.EndBlock)
                },
            )
            output.add(rewritten)
        }
        else -> output.add(instruction)
    }
}
