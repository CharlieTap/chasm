package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.Expression
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
        instructionSequencePredecoder = ::InstructionSequencePredecoder,
    )

internal inline fun ExpressionPredecoder(
    context: PredecodingContext,
    expression: Expression,
    crossinline instructionSequencePredecoder: Predecoder<List<io.github.charlietap.chasm.ir.instruction.Instruction>, Array<DispatchableInstruction>>,
): Result<RuntimeExpression, ModuleTrapError> = binding {
    RuntimeExpression(
        instructions = instructionSequencePredecoder(context, expression.instructions).bind(),
    )
}
