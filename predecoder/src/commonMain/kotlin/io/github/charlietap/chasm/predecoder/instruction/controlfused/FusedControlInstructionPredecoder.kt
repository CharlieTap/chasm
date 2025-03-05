package io.github.charlietap.chasm.predecoder.instruction.controlfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

internal fun FusedControlInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedControlInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedControlInstructionPredecoder(
        context = context,
        instruction = instruction,
        fusedBrIfPredecoder = ::FusedBrIfInstructionPredecoder,
        fusedCallPredecoder = ::FusedCallInstructionPredecoder,
        fusedIfPredecoder = ::FusedIfInstructionPredecoder,
    )

internal inline fun FusedControlInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedControlInstruction,
    crossinline fusedBrIfPredecoder: Predecoder<FusedControlInstruction.BrIf, DispatchableInstruction>,
    crossinline fusedCallPredecoder: Predecoder<FusedControlInstruction.Call, DispatchableInstruction>,
    crossinline fusedIfPredecoder: Predecoder<FusedControlInstruction.If, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedControlInstruction.BrIf -> fusedBrIfPredecoder(context, instruction).bind()
        is FusedControlInstruction.Call -> fusedCallPredecoder(context, instruction).bind()
        is FusedControlInstruction.If -> fusedIfPredecoder(context, instruction).bind()
    }
}
