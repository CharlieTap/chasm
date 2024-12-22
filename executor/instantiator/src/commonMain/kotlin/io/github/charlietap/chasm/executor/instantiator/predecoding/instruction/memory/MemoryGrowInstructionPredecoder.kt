package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryGrowDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.MemoryGrow

internal fun MemoryGrowInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.MemoryGrow,
): Result<DispatchableInstruction, ModuleTrapError> =
    MemoryGrowInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::MemoryGrowDispatcher,
    )

internal inline fun MemoryGrowInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.MemoryGrow,
    crossinline dispatcher: Dispatcher<MemoryGrow>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress).bind()

    dispatcher(MemoryGrow(memory))
}
