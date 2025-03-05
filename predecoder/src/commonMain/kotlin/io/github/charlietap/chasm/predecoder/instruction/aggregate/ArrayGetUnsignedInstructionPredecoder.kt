package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetUnsignedDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayGetUnsigned

internal fun ArrayGetUnsignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayGetUnsigned,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayGetUnsignedInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayGetUnsignedDispatcher,
    )

internal inline fun ArrayGetUnsignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayGetUnsigned,
    crossinline dispatcher: Dispatcher<ArrayGetUnsigned>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ArrayGetUnsigned(instruction.typeIndex))
}
