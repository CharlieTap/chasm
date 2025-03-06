package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrIfDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrIf

internal fun BrIfInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrIf,
): Result<DispatchableInstruction, ModuleTrapError> =
    BrIfInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::BrIfDispatcher,
    )

internal inline fun BrIfInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrIf,
    crossinline dispatcher: Dispatcher<BrIf>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(BrIf(instruction.labelIndex))
}
