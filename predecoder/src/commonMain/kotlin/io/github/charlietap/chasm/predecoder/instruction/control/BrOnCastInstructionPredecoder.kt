package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnCastDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrOnCast

internal fun BrOnCastInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrOnCast,
): Result<DispatchableInstruction, ModuleTrapError> =
    BrOnCastInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::BrOnCastDispatcher,
    )

internal inline fun BrOnCastInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrOnCast,
    crossinline dispatcher: Dispatcher<BrOnCast>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(
        BrOnCast(
            labelIndex = instruction.labelIndex,
            srcReferenceType = instruction.srcReferenceType,
            dstReferenceType = instruction.dstReferenceType,
        ),
    )
}
