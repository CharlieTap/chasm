package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayGet

internal fun ArrayGetInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayGet,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayGetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayGetDispatcher,
    )

internal inline fun ArrayGetInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayGet,
    crossinline dispatcher: Dispatcher<ArrayGet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ArrayGet(instruction.typeIndex))
}
