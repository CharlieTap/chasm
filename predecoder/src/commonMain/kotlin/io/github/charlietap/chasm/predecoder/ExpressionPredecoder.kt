package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.function.Expression as RuntimeExpression

fun ExpressionPredecoder(
    context: PredecodingContext,
    expression: Expression,
): Result<RuntimeExpression, ModuleTrapError> =
    ExpressionPredecoder(
        context = context,
        expression = expression,
        instructionPredecoder = ::InstructionPredecoder,
    )

internal inline fun ExpressionPredecoder(
    context: PredecodingContext,
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
