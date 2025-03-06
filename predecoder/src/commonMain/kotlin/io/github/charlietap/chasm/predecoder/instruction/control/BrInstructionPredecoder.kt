package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Br

internal fun BrInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Br,
): Result<DispatchableInstruction, ModuleTrapError> =
    BrInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::BrDispatcher,
    )

internal inline fun BrInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Br,
    crossinline dispatcher: Dispatcher<Br>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(Br(instruction.labelIndex))
} 
