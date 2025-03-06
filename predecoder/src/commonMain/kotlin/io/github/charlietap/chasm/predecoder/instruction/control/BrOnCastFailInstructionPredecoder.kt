package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnCastFailDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrOnCastFail

internal fun BrOnCastFailInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrOnCastFail,
): Result<DispatchableInstruction, ModuleTrapError> =
    BrOnCastFailInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::BrOnCastFailDispatcher,
    )

internal inline fun BrOnCastFailInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrOnCastFail,
    crossinline dispatcher: Dispatcher<BrOnCastFail>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(
        BrOnCastFail(
            labelIndex = instruction.labelIndex,
            srcReferenceType = instruction.srcReferenceType,
            dstReferenceType = instruction.dstReferenceType,
        ),
    )
}
