package io.github.charlietap.chasm.predecoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryCopyDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.MemoryCopy
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.memoryAddress

internal fun MemoryCopyInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.MemoryCopy,
): Result<DispatchableInstruction, ModuleTrapError> =
    MemoryCopyInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::MemoryCopyDispatcher,
    )

internal inline fun MemoryCopyInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.MemoryCopy,
    crossinline dispatcher: Dispatcher<MemoryCopy>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val srcMemoryAddress = context.instance.memoryAddress(instruction.srcIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val srcMemory = context.store.memory(srcMemoryAddress)

    val destMemoryAddress = context.instance.memoryAddress(instruction.dstIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val destMemory = context.store.memory(destMemoryAddress)

    dispatcher(MemoryCopy(srcMemory, destMemory))
}
