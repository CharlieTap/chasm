package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.ThrowRef

internal fun ThrowRefInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.ThrowRef,
): Result<DispatchableInstruction, ModuleTrapError> =
    ThrowRefInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ThrowRefDispatcher,
    )

internal inline fun ThrowRefInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.ThrowRef,
    crossinline dispatcher: Dispatcher<ThrowRef>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ThrowRef)
}
