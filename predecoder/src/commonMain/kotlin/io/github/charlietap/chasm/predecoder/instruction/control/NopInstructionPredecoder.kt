package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.NopDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Nop

internal fun NopInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Nop,
): Result<DispatchableInstruction, ModuleTrapError> =
    NopInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::NopDispatcher,
    )

internal inline fun NopInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Nop,
    crossinline dispatcher: Dispatcher<Nop>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(Nop)
}
