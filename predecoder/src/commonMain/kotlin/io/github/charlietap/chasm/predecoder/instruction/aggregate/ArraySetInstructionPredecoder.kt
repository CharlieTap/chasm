package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArraySetDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArraySet

internal fun ArraySetInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArraySet,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArraySetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArraySetDispatcher,
    )

internal inline fun ArraySetInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArraySet,
    crossinline dispatcher: Dispatcher<ArraySet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ArraySet(instruction.typeIndex))
}
