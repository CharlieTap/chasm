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
        fusedCallPredecoder = ::FusedCallInstructionPredecoder,
    )

internal inline fun FusedControlInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedControlInstruction,
    crossinline fusedCallPredecoder: Predecoder<FusedControlInstruction.Call, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedControlInstruction.Call -> fusedCallPredecoder(context, instruction).bind()
        else -> TODO()
    }
}
