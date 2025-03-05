package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ExternConvertAnyDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ExternConvertAny

internal fun ExternConvertAnyInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ExternConvertAny,
): Result<DispatchableInstruction, ModuleTrapError> =
    ExternConvertAnyInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ExternConvertAnyDispatcher,
    )

internal inline fun ExternConvertAnyInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ExternConvertAny,
    crossinline dispatcher: Dispatcher<ExternConvertAny>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ExternConvertAny)
}
