package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetSignedDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayGetSigned

internal fun ArrayGetSignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayGetSigned,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayGetSignedInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayGetSignedDispatcher,
    )

internal inline fun ArrayGetSignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayGetSigned,
    crossinline dispatcher: Dispatcher<ArrayGetSigned>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ArrayGetSigned(instruction.typeIndex))
}
