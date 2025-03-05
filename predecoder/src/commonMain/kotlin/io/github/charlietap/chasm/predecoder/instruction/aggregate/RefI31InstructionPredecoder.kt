package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.RefI31Dispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.RefI31

internal fun RefI31InstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.RefI31,
): Result<DispatchableInstruction, ModuleTrapError> =
    RefI31InstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::RefI31Dispatcher,
    )

internal inline fun RefI31InstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.RefI31,
    crossinline dispatcher: Dispatcher<RefI31>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(RefI31)
}
