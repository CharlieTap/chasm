package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.F32LoadInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.F64LoadInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I32Load16SInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I32Load16UInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I32Load8SInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I32Load8UInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I32LoadInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I64Load16SInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I64Load16UInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I64Load32SInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I64Load32UInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I64Load8SInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I64Load8UInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load.I64LoadInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store.F32StoreInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store.F64StoreInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store.I32Store16InstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store.I32Store8InstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store.I32StoreInstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store.I64Store16InstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store.I64Store32InstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store.I64Store8InstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store.I64StoreInstructionPredecoder
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError

internal fun MemoryInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    MemoryInstructionPredecoder(
        context = context,
        instruction = instruction,
        i32LoadPredecoder = ::I32LoadInstructionPredecoder,
        i64LoadPredecoder = ::I64LoadInstructionPredecoder,
        f32LoadPredecoder = ::F32LoadInstructionPredecoder,
        f64LoadPredecoder = ::F64LoadInstructionPredecoder,
        i32Load8SPredecoder = ::I32Load8SInstructionPredecoder,
        i32Load8UPredecoder = ::I32Load8UInstructionPredecoder,
        i32Load16SPredecoder = ::I32Load16SInstructionPredecoder,
        i32Load16UPredecoder = ::I32Load16UInstructionPredecoder,
        i64Load8SPredecoder = ::I64Load8SInstructionPredecoder,
        i64Load8UPredecoder = ::I64Load8UInstructionPredecoder,
        i64Load16SPredecoder = ::I64Load16SInstructionPredecoder,
        i64Load16UPredecoder = ::I64Load16UInstructionPredecoder,
        i64Load32SPredecoder = ::I64Load32SInstructionPredecoder,
        i64Load32UPredecoder = ::I64Load32UInstructionPredecoder,
        i32StorePredecoder = ::I32StoreInstructionPredecoder,
        i64StorePredecoder = ::I64StoreInstructionPredecoder,
        f32StorePredecoder = ::F32StoreInstructionPredecoder,
        f64StorePredecoder = ::F64StoreInstructionPredecoder,
        i32Store8Predecoder = ::I32Store8InstructionPredecoder,
        i32Store16Predecoder = ::I32Store16InstructionPredecoder,
        i64Store8Predecoder = ::I64Store8InstructionPredecoder,
        i64Store16Predecoder = ::I64Store16InstructionPredecoder,
        i64Store32Predecoder = ::I64Store32InstructionPredecoder,
        memorySizePredecoder = ::MemorySizeInstructionPredecoder,
        memoryGrowPredecoder = ::MemoryGrowInstructionPredecoder,
        memoryInitPredecoder = ::MemoryInitInstructionPredecoder,
        dataDropPredecoder = ::DataDropInstructionPredecoder,
        memoryCopyPredecoder = ::MemoryCopyInstructionPredecoder,
        memoryFillPredecoder = ::MemoryFillInstructionPredecoder,
    )

internal inline fun MemoryInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction,
    crossinline i32LoadPredecoder: Predecoder<MemoryInstruction.I32Load, DispatchableInstruction>,
    crossinline i64LoadPredecoder: Predecoder<MemoryInstruction.I64Load, DispatchableInstruction>,
    crossinline f32LoadPredecoder: Predecoder<MemoryInstruction.F32Load, DispatchableInstruction>,
    crossinline f64LoadPredecoder: Predecoder<MemoryInstruction.F64Load, DispatchableInstruction>,
    crossinline i32Load8SPredecoder: Predecoder<MemoryInstruction.I32Load8S, DispatchableInstruction>,
    crossinline i32Load8UPredecoder: Predecoder<MemoryInstruction.I32Load8U, DispatchableInstruction>,
    crossinline i32Load16SPredecoder: Predecoder<MemoryInstruction.I32Load16S, DispatchableInstruction>,
    crossinline i32Load16UPredecoder: Predecoder<MemoryInstruction.I32Load16U, DispatchableInstruction>,
    crossinline i64Load8SPredecoder: Predecoder<MemoryInstruction.I64Load8S, DispatchableInstruction>,
    crossinline i64Load8UPredecoder: Predecoder<MemoryInstruction.I64Load8U, DispatchableInstruction>,
    crossinline i64Load16SPredecoder: Predecoder<MemoryInstruction.I64Load16S, DispatchableInstruction>,
    crossinline i64Load16UPredecoder: Predecoder<MemoryInstruction.I64Load16U, DispatchableInstruction>,
    crossinline i64Load32SPredecoder: Predecoder<MemoryInstruction.I64Load32S, DispatchableInstruction>,
    crossinline i64Load32UPredecoder: Predecoder<MemoryInstruction.I64Load32U, DispatchableInstruction>,
    crossinline i32StorePredecoder: Predecoder<MemoryInstruction.I32Store, DispatchableInstruction>,
    crossinline i64StorePredecoder: Predecoder<MemoryInstruction.I64Store, DispatchableInstruction>,
    crossinline f32StorePredecoder: Predecoder<MemoryInstruction.F32Store, DispatchableInstruction>,
    crossinline f64StorePredecoder: Predecoder<MemoryInstruction.F64Store, DispatchableInstruction>,
    crossinline i32Store8Predecoder: Predecoder<MemoryInstruction.I32Store8, DispatchableInstruction>,
    crossinline i32Store16Predecoder: Predecoder<MemoryInstruction.I32Store16, DispatchableInstruction>,
    crossinline i64Store8Predecoder: Predecoder<MemoryInstruction.I64Store8, DispatchableInstruction>,
    crossinline i64Store16Predecoder: Predecoder<MemoryInstruction.I64Store16, DispatchableInstruction>,
    crossinline i64Store32Predecoder: Predecoder<MemoryInstruction.I64Store32, DispatchableInstruction>,
    crossinline memorySizePredecoder: Predecoder<MemoryInstruction.MemorySize, DispatchableInstruction>,
    crossinline memoryGrowPredecoder: Predecoder<MemoryInstruction.MemoryGrow, DispatchableInstruction>,
    crossinline memoryInitPredecoder: Predecoder<MemoryInstruction.MemoryInit, DispatchableInstruction>,
    crossinline dataDropPredecoder: Predecoder<MemoryInstruction.DataDrop, DispatchableInstruction>,
    crossinline memoryCopyPredecoder: Predecoder<MemoryInstruction.MemoryCopy, DispatchableInstruction>,
    crossinline memoryFillPredecoder: Predecoder<MemoryInstruction.MemoryFill, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is MemoryInstruction.I32Load -> i32LoadPredecoder(context, instruction).bind()
        is MemoryInstruction.I64Load -> i64LoadPredecoder(context, instruction).bind()
        is MemoryInstruction.F32Load -> f32LoadPredecoder(context, instruction).bind()
        is MemoryInstruction.F64Load -> f64LoadPredecoder(context, instruction).bind()
        is MemoryInstruction.I32Load8S -> i32Load8SPredecoder(context, instruction).bind()
        is MemoryInstruction.I32Load8U -> i32Load8UPredecoder(context, instruction).bind()
        is MemoryInstruction.I32Load16S -> i32Load16SPredecoder(context, instruction).bind()
        is MemoryInstruction.I32Load16U -> i32Load16UPredecoder(context, instruction).bind()
        is MemoryInstruction.I64Load8S -> i64Load8SPredecoder(context, instruction).bind()
        is MemoryInstruction.I64Load8U -> i64Load8UPredecoder(context, instruction).bind()
        is MemoryInstruction.I64Load16S -> i64Load16SPredecoder(context, instruction).bind()
        is MemoryInstruction.I64Load16U -> i64Load16UPredecoder(context, instruction).bind()
        is MemoryInstruction.I64Load32S -> i64Load32SPredecoder(context, instruction).bind()
        is MemoryInstruction.I64Load32U -> i64Load32UPredecoder(context, instruction).bind()
        is MemoryInstruction.I32Store -> i32StorePredecoder(context, instruction).bind()
        is MemoryInstruction.I64Store -> i64StorePredecoder(context, instruction).bind()
        is MemoryInstruction.F32Store -> f32StorePredecoder(context, instruction).bind()
        is MemoryInstruction.F64Store -> f64StorePredecoder(context, instruction).bind()
        is MemoryInstruction.I32Store8 -> i32Store8Predecoder(context, instruction).bind()
        is MemoryInstruction.I32Store16 -> i32Store16Predecoder(context, instruction).bind()
        is MemoryInstruction.I64Store8 -> i64Store8Predecoder(context, instruction).bind()
        is MemoryInstruction.I64Store16 -> i64Store16Predecoder(context, instruction).bind()
        is MemoryInstruction.I64Store32 -> i64Store32Predecoder(context, instruction).bind()
        is MemoryInstruction.MemorySize -> memorySizePredecoder(context, instruction).bind()
        is MemoryInstruction.MemoryGrow -> memoryGrowPredecoder(context, instruction).bind()
        is MemoryInstruction.MemoryInit -> memoryInitPredecoder(context, instruction).bind()
        is MemoryInstruction.DataDrop -> dataDropPredecoder(context, instruction).bind()
        is MemoryInstruction.MemoryCopy -> memoryCopyPredecoder(context, instruction).bind()
        is MemoryInstruction.MemoryFill -> memoryFillPredecoder(context, instruction).bind()
    }
}
