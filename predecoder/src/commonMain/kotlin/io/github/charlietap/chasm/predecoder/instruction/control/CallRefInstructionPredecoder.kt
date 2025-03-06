package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallRefDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.CallRef

internal fun CallRefInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.CallRef,
): Result<DispatchableInstruction, ModuleTrapError> =
    CallRefInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::CallRefDispatcher,
    )

internal inline fun CallRefInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.CallRef,
    crossinline dispatcher: Dispatcher<CallRef>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(CallRef(instruction.typeIndex))
} 
