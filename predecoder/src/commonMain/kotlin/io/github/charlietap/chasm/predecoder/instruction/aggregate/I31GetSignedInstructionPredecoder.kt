package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.I31GetSignedDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.I31GetSigned

internal fun I31GetSignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.I31GetSigned,
): Result<DispatchableInstruction, ModuleTrapError> =
    I31GetSignedInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I31GetSignedDispatcher,
    )

internal inline fun I31GetSignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.I31GetSigned,
    crossinline dispatcher: Dispatcher<I31GetSigned>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(I31GetSigned)
}
