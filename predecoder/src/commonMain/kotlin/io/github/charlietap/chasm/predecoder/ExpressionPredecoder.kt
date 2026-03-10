package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.execution.InterpretationStyle
import io.github.charlietap.chasm.runtime.function.FusedIpBody
import io.github.charlietap.chasm.runtime.function.Expression as RuntimeExpression

fun ExpressionPredecoder(
    context: PredecodingContext,
    expression: Expression,
    enableFusedIp: Boolean = false,
): Result<RuntimeExpression, ModuleTrapError> =
    ExpressionPredecoder(
        context = context,
        expression = expression,
        enableFusedIp = enableFusedIp,
        instructionSequencePredecoder = ::InstructionSequencePredecoder,
        fusedIpBodyPredecoder = ::FusedIpBodyPredecoder,
    )

internal inline fun ExpressionPredecoder(
    context: PredecodingContext,
    expression: Expression,
    enableFusedIp: Boolean = false,
    crossinline instructionSequencePredecoder: Predecoder<List<io.github.charlietap.chasm.ir.instruction.Instruction>, Array<DispatchableInstruction>>,
    crossinline fusedIpBodyPredecoder: Predecoder<List<io.github.charlietap.chasm.ir.instruction.Instruction>, FusedIpBody?>,
): Result<RuntimeExpression, ModuleTrapError> = binding {
    val instructions = instructionSequencePredecoder(context, expression.instructions).bind()
    val fusedIpBody = if (enableFusedIp) {
        fusedIpBodyPredecoder(context, expression.instructions).bind()
    } else {
        null
    }

    RuntimeExpression(
        instructions = instructions,
        interpretationStyle = if (fusedIpBody != null) InterpretationStyle.INSTRUCTION_POINTER else InterpretationStyle.INSTRUCTION_STACK,
        fusedIpBody = fusedIpBody,
    )
}
