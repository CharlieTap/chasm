package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayCopyDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayCopy

internal fun ArrayCopyInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayCopy,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayCopyInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayCopyDispatcher,
    )

internal inline fun ArrayCopyInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayCopy,
    crossinline dispatcher: Dispatcher<ArrayCopy>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ArrayCopy(instruction.sourceTypeIndex, instruction.destinationTypeIndex))
}
