package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnNullDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrOnNull

internal fun BrOnNullInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrOnNull,
): Result<DispatchableInstruction, ModuleTrapError> =
    BrOnNullInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::BrOnNullDispatcher,
    )

internal inline fun BrOnNullInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrOnNull,
    crossinline dispatcher: Dispatcher<BrOnNull>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(BrOnNull(instruction.labelIndex))
}
