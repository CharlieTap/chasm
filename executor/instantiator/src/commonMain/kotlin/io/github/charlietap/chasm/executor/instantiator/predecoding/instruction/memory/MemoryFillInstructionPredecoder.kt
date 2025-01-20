package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.memoryAddress
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.MemoryFill

internal fun MemoryFillInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.MemoryFill,
): Result<DispatchableInstruction, ModuleTrapError> =
    MemoryFillInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::MemoryFillDispatcher,
    )

internal inline fun MemoryFillInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.MemoryFill,
    crossinline dispatcher: Dispatcher<MemoryFill>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)

    dispatcher(MemoryFill(memory))
}
