package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I32LoadInstructionPredecoder
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction

internal fun FusedMemoryInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedMemoryInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedMemoryInstructionPredecoder(
        context = context,
        instruction = instruction,
        i32LoadInstructionPredecoder = ::I32LoadInstructionPredecoder,
    )

internal inline fun FusedMemoryInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedMemoryInstruction,
    crossinline i32LoadInstructionPredecoder: Predecoder<FusedMemoryInstruction.I32Load, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedMemoryInstruction.F32Load -> TODO()
        is FusedMemoryInstruction.F32Store -> TODO()
        is FusedMemoryInstruction.F64Load -> TODO()
        is FusedMemoryInstruction.F64Store -> TODO()
        is FusedMemoryInstruction.I32Load -> i32LoadInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I32Load16S -> TODO()
        is FusedMemoryInstruction.I32Load16U -> TODO()
        is FusedMemoryInstruction.I32Load8S -> TODO()
        is FusedMemoryInstruction.I32Load8U -> TODO()
        is FusedMemoryInstruction.I32Store -> TODO()
        is FusedMemoryInstruction.I32Store16 -> TODO()
        is FusedMemoryInstruction.I32Store8 -> TODO()
        is FusedMemoryInstruction.I64Load -> TODO()
        is FusedMemoryInstruction.I64Load16S -> TODO()
        is FusedMemoryInstruction.I64Load16U -> TODO()
        is FusedMemoryInstruction.I64Load32S -> TODO()
        is FusedMemoryInstruction.I64Load32U -> TODO()
        is FusedMemoryInstruction.I64Load8S -> TODO()
        is FusedMemoryInstruction.I64Load8U -> TODO()
        is FusedMemoryInstruction.I64Store -> TODO()
        is FusedMemoryInstruction.I64Store16 -> TODO()
        is FusedMemoryInstruction.I64Store32 -> TODO()
        is FusedMemoryInstruction.I64Store8 -> TODO()
    }
}
