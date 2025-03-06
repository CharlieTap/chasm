package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnCallRefDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.ReturnCallRef

internal fun ReturnCallRefInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.ReturnCallRef,
): Result<DispatchableInstruction, ModuleTrapError> =
    ReturnCallRefInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ReturnCallRefDispatcher,
    )

internal inline fun ReturnCallRefInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.ReturnCallRef,
    crossinline dispatcher: Dispatcher<ReturnCallRef>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ReturnCallRef(instruction.typeIndex))
} 
