package io.github.charlietap.chasm.compiler.passes.controlflow

import io.github.charlietap.chasm.compiler.passes.PassContext
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction

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
    val preserveStructuredEndBlocks = !context.config.bytecodeFusion
    when (instruction) {
        is ControlInstruction.Block -> {

            val expression = expressionRewriter(context, Expression(instruction.instructions))

            val rewritten = instruction.copy(
                instructions = if (preserveStructuredEndBlocks) {
                    buildList {
                        addAll(expression.instructions)
                        add(AdminInstruction.EndBlock)
                    }
                } else {
                    expression.instructions
                },
            )
            output.add(rewritten)
        }
        is ControlInstruction.If -> {

            val expression = expressionRewriter(context, Expression(instruction.thenInstructions))

            val rewritten = instruction.copy(
                thenInstructions = if (preserveStructuredEndBlocks) {
                    buildList {
                        addAll(expression.instructions)
                        add(AdminInstruction.EndBlock)
                    }
                } else {
                    expression.instructions
                },
                elseInstructions = if (preserveStructuredEndBlocks) {
                    buildList {
                        instruction.elseInstructions?.let {
                            val elseExpression = expressionRewriter(context, Expression(it))
                            addAll(elseExpression.instructions)
                        }
                        add(AdminInstruction.EndBlock)
                    }
                } else {
                    buildList {
                        instruction.elseInstructions?.let { elseInstructions ->
                            addAll(expressionRewriter(context, Expression(elseInstructions)).instructions)
                        }
                    }
                },
            )
            output.add(rewritten)
        }
        is ControlInstruction.Loop -> {
            val expression = expressionRewriter(context, Expression(instruction.instructions))

            val rewritten = instruction.copy(
                instructions = if (preserveStructuredEndBlocks) {
                    buildList {
                        addAll(expression.instructions)
                        add(AdminInstruction.EndBlock)
                    }
                } else {
                    expression.instructions
                },
            )
            output.add(rewritten)
        }
        is ControlInstruction.TryTable -> {
            val expression = expressionRewriter(context, Expression(instruction.instructions))

            val rewritten = instruction.copy(
                instructions = if (preserveStructuredEndBlocks) {
                    buildList {
                        addAll(expression.instructions)
                        add(AdminInstruction.EndBlock)
                    }
                } else {
                    expression.instructions
                },
            )
            output.add(rewritten)
        }
        else -> output.add(instruction)
    }
}
