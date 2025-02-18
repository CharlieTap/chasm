package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.controlfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction

internal fun FusedControlInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedControlInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedControlInstructionPredecoder(
        context = context,
        instruction = instruction,
        fusedIfPredecoder = ::FusedIfInstructionPredecoder,
    )

internal inline fun FusedControlInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedControlInstruction,
    crossinline fusedIfPredecoder: Predecoder<FusedControlInstruction.If, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedControlInstruction.If -> fusedIfPredecoder(context, instruction).bind()
    }
}
