package io.github.charlietap.chasm.executor.instantiator.predecoding

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.function.Expression as RuntimeExpression

internal fun ExpressionPredecoder(
    context: InstantiationContext,
    expression: Expression,
): Result<RuntimeExpression, ModuleTrapError> =
    ExpressionPredecoder(
        context = context,
        expression = expression,
        instructionPredecoder = ::InstructionPredecoder,
    )

internal inline fun ExpressionPredecoder(
    context: InstantiationContext,
    expression: Expression,
    crossinline instructionPredecoder: Predecoder<Instruction, DispatchableInstruction>,
): Result<RuntimeExpression, ModuleTrapError> = binding {

    val instructions: Array<DispatchableInstruction> = Array(expression.instructions.size) { idx ->
        val reversedIndex = expression.instructions.size - 1 - idx
        val predispatch = expression.instructions[reversedIndex]
        instructionPredecoder(context, predispatch).bind()
    }

    RuntimeExpression(
        instructions = instructions,
    )
}
