package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.function.Expression as RuntimeExpression

fun ExpressionPredecoder(
    context: PredecodingContext,
    expression: Expression,
): Result<RuntimeExpression, ModuleTrapError> =
    binding {
        val baseIp = context.store.program.size
        val instructions = InstructionSequencePredecoder(context, expression.instructions, baseIp).bind()
        val entryIp = if (instructions.isEmpty()) EXIT_IP else context.store.program.append(instructions)
        RuntimeExpression(entryIp)
    }
