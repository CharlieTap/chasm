package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Return

internal fun ReturnInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Return,
): Result<DispatchableInstruction, ModuleTrapError> =
    ReturnInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ReturnDispatcher,
    )

internal inline fun ReturnInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Return,
    crossinline dispatcher: Dispatcher<Return>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(Return)
} 
