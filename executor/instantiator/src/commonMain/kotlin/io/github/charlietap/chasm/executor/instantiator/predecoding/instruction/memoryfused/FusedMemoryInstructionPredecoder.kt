package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.F32LoadInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.F64LoadInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I32Load16SInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I32Load16UInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I32Load8SInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I32Load8UInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I32LoadInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I64Load16SInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I64Load16UInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I64Load32SInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I64Load32UInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I64Load8SInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I64Load8UInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load.I64LoadInstructionPredecoder
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
        i32Load8SInstructionPredecoder = ::I32Load8SInstructionPredecoder,
        i32Load8UInstructionPredecoder = ::I32Load8UInstructionPredecoder,
        i32Load16SInstructionPredecoder = ::I32Load16SInstructionPredecoder,
        i32Load16UInstructionPredecoder = ::I32Load16UInstructionPredecoder,
        i64LoadInstructionPredecoder = ::I64LoadInstructionPredecoder,
        i64Load8SInstructionPredecoder = ::I64Load8SInstructionPredecoder,
        i64Load8UInstructionPredecoder = ::I64Load8UInstructionPredecoder,
        i64Load16SInstructionPredecoder = ::I64Load16SInstructionPredecoder,
        i64Load16UInstructionPredecoder = ::I64Load16UInstructionPredecoder,
        i64Load32SInstructionPredecoder = ::I64Load32SInstructionPredecoder,
        i64Load32UInstructionPredecoder = ::I64Load32UInstructionPredecoder,
        f32LoadInstructionPredecoder = ::F32LoadInstructionPredecoder,
        f64LoadInstructionPredecoder = ::F64LoadInstructionPredecoder,
    )

internal inline fun FusedMemoryInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedMemoryInstruction,
    crossinline i32LoadInstructionPredecoder: Predecoder<FusedMemoryInstruction.I32Load, DispatchableInstruction>,
    crossinline i32Load8SInstructionPredecoder: Predecoder<FusedMemoryInstruction.I32Load8S, DispatchableInstruction>,
    crossinline i32Load8UInstructionPredecoder: Predecoder<FusedMemoryInstruction.I32Load8U, DispatchableInstruction>,
    crossinline i32Load16SInstructionPredecoder: Predecoder<FusedMemoryInstruction.I32Load16S, DispatchableInstruction>,
    crossinline i32Load16UInstructionPredecoder: Predecoder<FusedMemoryInstruction.I32Load16U, DispatchableInstruction>,
    crossinline i64LoadInstructionPredecoder: Predecoder<FusedMemoryInstruction.I64Load, DispatchableInstruction>,
    crossinline i64Load8SInstructionPredecoder: Predecoder<FusedMemoryInstruction.I64Load8S, DispatchableInstruction>,
    crossinline i64Load8UInstructionPredecoder: Predecoder<FusedMemoryInstruction.I64Load8U, DispatchableInstruction>,
    crossinline i64Load16SInstructionPredecoder: Predecoder<FusedMemoryInstruction.I64Load16S, DispatchableInstruction>,
    crossinline i64Load16UInstructionPredecoder: Predecoder<FusedMemoryInstruction.I64Load16U, DispatchableInstruction>,
    crossinline i64Load32SInstructionPredecoder: Predecoder<FusedMemoryInstruction.I64Load32S, DispatchableInstruction>,
    crossinline i64Load32UInstructionPredecoder: Predecoder<FusedMemoryInstruction.I64Load32U, DispatchableInstruction>,
    crossinline f32LoadInstructionPredecoder: Predecoder<FusedMemoryInstruction.F32Load, DispatchableInstruction>,
    crossinline f64LoadInstructionPredecoder: Predecoder<FusedMemoryInstruction.F64Load, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedMemoryInstruction.I32Load -> i32LoadInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I32Load8S -> i32Load8SInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I32Load8U -> i32Load8UInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I32Load16S -> i32Load16SInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I32Load16U -> i32Load16UInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I64Load -> i64LoadInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I64Load8S -> i64Load8SInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I64Load8U -> i64Load8UInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I64Load16S -> i64Load16SInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I64Load16U -> i64Load16UInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I64Load32S -> i64Load32SInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.I64Load32U -> i64Load32UInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.F32Load -> f32LoadInstructionPredecoder(context, instruction).bind()
        is FusedMemoryInstruction.F64Load -> f64LoadInstructionPredecoder(context, instruction).bind()

        is FusedMemoryInstruction.I32Store -> TODO()
        is FusedMemoryInstruction.I32Store8 -> TODO()
        is FusedMemoryInstruction.I32Store16 -> TODO()
        is FusedMemoryInstruction.I64Store -> TODO()
        is FusedMemoryInstruction.I64Store8 -> TODO()
        is FusedMemoryInstruction.I64Store16 -> TODO()
        is FusedMemoryInstruction.I64Store32 -> TODO()
        is FusedMemoryInstruction.F32Store -> TODO()
        is FusedMemoryInstruction.F64Store -> TODO()
    }
}
