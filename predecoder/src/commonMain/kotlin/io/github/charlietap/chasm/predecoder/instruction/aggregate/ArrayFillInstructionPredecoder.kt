package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayFillDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayFill

internal fun ArrayFillInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayFill,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayFillInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayFillDispatcher,
    )

internal inline fun ArrayFillInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayFill,
    crossinline dispatcher: Dispatcher<ArrayFill>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ArrayFill(instruction.typeIndex))
}
