package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayLenDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayLen

internal fun ArrayLenInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayLen,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayLenInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayLenDispatcher,
    )

internal inline fun ArrayLenInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayLen,
    crossinline dispatcher: Dispatcher<ArrayLen>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ArrayLen)
}
