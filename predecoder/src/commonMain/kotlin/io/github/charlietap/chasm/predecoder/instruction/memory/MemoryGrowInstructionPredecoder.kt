package io.github.charlietap.chasm.predecoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryGrowDispatcher
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction.MemoryGrow
import io.github.charlietap.chasm.runtime.memory.LinearMemory

internal fun MemoryGrowInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.MemoryGrow,
): Result<DispatchableInstruction, ModuleTrapError> =
    MemoryGrowInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::MemoryGrowDispatcher,
    )

internal inline fun MemoryGrowInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.MemoryGrow,
    crossinline dispatcher: Dispatcher<MemoryGrow>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val max = memory.type.limits.max
        ?.toInt() ?: LinearMemory.MAX_PAGES

    dispatcher(MemoryGrow(memory, max))
}
