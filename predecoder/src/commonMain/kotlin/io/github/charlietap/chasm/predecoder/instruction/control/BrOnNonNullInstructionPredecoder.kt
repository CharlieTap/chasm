package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnNonNullDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrOnNonNull

internal fun BrOnNonNullInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrOnNonNull,
): Result<DispatchableInstruction, ModuleTrapError> =
    BrOnNonNullInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::BrOnNonNullDispatcher,
    )

internal inline fun BrOnNonNullInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrOnNonNull,
    crossinline dispatcher: Dispatcher<BrOnNonNull>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(BrOnNonNull(instruction.labelIndex))
}
