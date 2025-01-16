package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInitDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.dataAddress
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.MemoryInit

internal fun MemoryInitInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.MemoryInit,
): Result<DispatchableInstruction, ModuleTrapError> =
    MemoryInitInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::MemoryInitDispatcher,
    )

internal inline fun MemoryInitInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.MemoryInit,
    crossinline dispatcher: Dispatcher<MemoryInit>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)

    val dataAddress = context.instance?.dataAddress(instruction.dataIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val data = context.store.data(dataAddress)

    dispatcher(MemoryInit(memory, data))
}
