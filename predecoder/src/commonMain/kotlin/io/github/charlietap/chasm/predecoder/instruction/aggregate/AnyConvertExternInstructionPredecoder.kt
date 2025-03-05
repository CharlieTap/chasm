package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.AnyConvertExternDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.AnyConvertExtern

internal fun AnyConvertExternInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.AnyConvertExtern,
): Result<DispatchableInstruction, ModuleTrapError> =
    AnyConvertExternInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::AnyConvertExternDispatcher,
    )

internal inline fun AnyConvertExternInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.AnyConvertExtern,
    crossinline dispatcher: Dispatcher<AnyConvertExtern>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(AnyConvertExtern)
}
