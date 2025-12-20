package io.github.charlietap.chasm.predecoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillDispatcher
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction.MemoryFill

internal fun MemoryFillInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.MemoryFill,
): Result<DispatchableInstruction, ModuleTrapError> =
    MemoryFillInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::MemoryFillDispatcher,
    )

internal inline fun MemoryFillInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.MemoryFill,
    crossinline dispatcher: Dispatcher<MemoryFill>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(instruction.memoryIndex).bind()
    val memory = context.store.memory(memoryAddress)

    dispatcher(MemoryFill(memory))
}
