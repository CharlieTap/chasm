package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32LoadDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32StoreDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F64LoadDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F64StoreDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load16SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load16UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load8SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load8UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32LoadDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Store16Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Store8Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32StoreDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load16SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load16UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load8SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load8UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64LoadDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store16Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store32Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store8Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64StoreDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryCopyDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInitDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemorySizeDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.F32Load
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.F32Store
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.F64Load
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.F64Store
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Load
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Load16S
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Load16U
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Load8S
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Load8U
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Store
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Store16
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Store8
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Load
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Load16S
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Load16U
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Load32S
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Load32U
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Load8S
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Load8U
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Store
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Store16
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Store32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Store8
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.MemoryCopy
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.MemoryFill
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.MemoryInit
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.MemorySize

internal fun MemoryInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    MemoryInstructionPredecoder(
        context = context,
        instruction = instruction,
        i32LoadDispatcher = ::I32LoadDispatcher,
        i64LoadDispatcher = ::I64LoadDispatcher,
        f32LoadDispatcher = ::F32LoadDispatcher,
        f64LoadDispatcher = ::F64LoadDispatcher,
        i32Load8SDispatcher = ::I32Load8SDispatcher,
        i32Load8UDispatcher = ::I32Load8UDispatcher,
        i32Load16SDispatcher = ::I32Load16SDispatcher,
        i32Load16UDispatcher = ::I32Load16UDispatcher,
        i64Load8SDispatcher = ::I64Load8SDispatcher,
        i64Load8UDispatcher = ::I64Load8UDispatcher,
        i64Load16SDispatcher = ::I64Load16SDispatcher,
        i64Load16UDispatcher = ::I64Load16UDispatcher,
        i64Load32SDispatcher = ::I64Load32SDispatcher,
        i64Load32UDispatcher = ::I64Load32UDispatcher,
        i32StoreDispatcher = ::I32StoreDispatcher,
        i64StoreDispatcher = ::I64StoreDispatcher,
        f32StoreDispatcher = ::F32StoreDispatcher,
        f64StoreDispatcher = ::F64StoreDispatcher,
        i32Store8Dispatcher = ::I32Store8Dispatcher,
        i32Store16Dispatcher = ::I32Store16Dispatcher,
        i64Store8Dispatcher = ::I64Store8Dispatcher,
        i64Store16Dispatcher = ::I64Store16Dispatcher,
        i64Store32Dispatcher = ::I64Store32Dispatcher,
        memorySizeDispatcher = ::MemorySizeDispatcher,
        memoryGrowPredecoder = ::MemoryGrowInstructionPredecoder,
        memoryInitDispatcher = ::MemoryInitDispatcher,
        dataDropPredecoder = ::DataDropInstructionPredecoder,
        memoryCopyDispatcher = ::MemoryCopyDispatcher,
        memoryFillDispatcher = ::MemoryFillDispatcher,
    )

internal inline fun MemoryInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction,
    crossinline i32LoadDispatcher: Dispatcher<I32Load>,
    crossinline i64LoadDispatcher: Dispatcher<I64Load>,
    crossinline f32LoadDispatcher: Dispatcher<F32Load>,
    crossinline f64LoadDispatcher: Dispatcher<F64Load>,
    crossinline i32Load8SDispatcher: Dispatcher<I32Load8S>,
    crossinline i32Load8UDispatcher: Dispatcher<I32Load8U>,
    crossinline i32Load16SDispatcher: Dispatcher<I32Load16S>,
    crossinline i32Load16UDispatcher: Dispatcher<I32Load16U>,
    crossinline i64Load8SDispatcher: Dispatcher<I64Load8S>,
    crossinline i64Load8UDispatcher: Dispatcher<I64Load8U>,
    crossinline i64Load16SDispatcher: Dispatcher<I64Load16S>,
    crossinline i64Load16UDispatcher: Dispatcher<I64Load16U>,
    crossinline i64Load32SDispatcher: Dispatcher<I64Load32S>,
    crossinline i64Load32UDispatcher: Dispatcher<I64Load32U>,
    crossinline i32StoreDispatcher: Dispatcher<I32Store>,
    crossinline i64StoreDispatcher: Dispatcher<I64Store>,
    crossinline f32StoreDispatcher: Dispatcher<F32Store>,
    crossinline f64StoreDispatcher: Dispatcher<F64Store>,
    crossinline i32Store8Dispatcher: Dispatcher<I32Store8>,
    crossinline i32Store16Dispatcher: Dispatcher<I32Store16>,
    crossinline i64Store8Dispatcher: Dispatcher<I64Store8>,
    crossinline i64Store16Dispatcher: Dispatcher<I64Store16>,
    crossinline i64Store32Dispatcher: Dispatcher<I64Store32>,
    crossinline memorySizeDispatcher: Dispatcher<MemorySize>,
    crossinline memoryGrowPredecoder: Predecoder<MemoryInstruction.MemoryGrow, DispatchableInstruction>,
    crossinline memoryInitDispatcher: Dispatcher<MemoryInit>,
    crossinline dataDropPredecoder: Predecoder<MemoryInstruction.DataDrop, DispatchableInstruction>,
    crossinline memoryCopyDispatcher: Dispatcher<MemoryCopy>,
    crossinline memoryFillDispatcher: Dispatcher<MemoryFill>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is MemoryInstruction.I32Load -> i32LoadDispatcher(I32Load(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Load -> i64LoadDispatcher(I64Load(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.F32Load -> f32LoadDispatcher(F32Load(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.F64Load -> f64LoadDispatcher(F64Load(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I32Load8S -> i32Load8SDispatcher(I32Load8S(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I32Load8U -> i32Load8UDispatcher(I32Load8U(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I32Load16S -> i32Load16SDispatcher(I32Load16S(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I32Load16U -> i32Load16UDispatcher(I32Load16U(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Load8S -> i64Load8SDispatcher(I64Load8S(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Load8U -> i64Load8UDispatcher(I64Load8U(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Load16S -> i64Load16SDispatcher(I64Load16S(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Load16U -> i64Load16UDispatcher(I64Load16U(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Load32S -> i64Load32SDispatcher(I64Load32S(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Load32U -> i64Load32UDispatcher(I64Load32U(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I32Store -> i32StoreDispatcher(I32Store(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Store -> i64StoreDispatcher(I64Store(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.F32Store -> f32StoreDispatcher(F32Store(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.F64Store -> f64StoreDispatcher(F64Store(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I32Store8 -> i32Store8Dispatcher(I32Store8(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I32Store16 -> i32Store16Dispatcher(I32Store16(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Store8 -> i64Store8Dispatcher(I64Store8(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Store16 -> i64Store16Dispatcher(I64Store16(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.I64Store32 -> i64Store32Dispatcher(I64Store32(instruction.memoryIndex, instruction.memArg))
        is MemoryInstruction.MemorySize -> memorySizeDispatcher(MemorySize(instruction.memoryIndex))
        is MemoryInstruction.MemoryGrow -> memoryGrowPredecoder(context, instruction).bind()
        is MemoryInstruction.MemoryInit -> memoryInitDispatcher(MemoryInit(instruction.memoryIndex, instruction.dataIndex))
        is MemoryInstruction.DataDrop -> dataDropPredecoder(context, instruction).bind()
        is MemoryInstruction.MemoryCopy -> memoryCopyDispatcher(MemoryCopy(instruction.srcIndex, instruction.dstIndex))
        is MemoryInstruction.MemoryFill -> memoryFillDispatcher(MemoryFill(instruction.memoryIndex))
    }
}
