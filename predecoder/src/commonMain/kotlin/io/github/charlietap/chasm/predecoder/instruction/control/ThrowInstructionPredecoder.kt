package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Throw

internal fun ThrowInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Throw,
): Result<DispatchableInstruction, ModuleTrapError> =
    ThrowInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ThrowDispatcher,
    )

internal inline fun ThrowInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Throw,
    crossinline dispatcher: Dispatcher<Throw>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(Throw(instruction.tagIndex))
} 
