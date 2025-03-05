package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.I31GetUnsignedDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.I31GetUnsigned

internal fun I31GetUnsignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.I31GetUnsigned,
): Result<DispatchableInstruction, ModuleTrapError> =
    I31GetUnsignedInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I31GetUnsignedDispatcher,
    )

internal inline fun I31GetUnsignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.I31GetUnsigned,
    crossinline dispatcher: Dispatcher<I31GetUnsigned>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(I31GetUnsigned)
}
