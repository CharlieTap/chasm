package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructGetSignedDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructGetSigned

internal fun StructGetSignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.StructGetSigned,
): Result<DispatchableInstruction, ModuleTrapError> =
    StructGetSignedInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::StructGetSignedDispatcher,
    )

internal inline fun StructGetSignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.StructGetSigned,
    crossinline dispatcher: Dispatcher<StructGetSigned>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(StructGetSigned(instruction.typeIndex, instruction.fieldIndex))
}
