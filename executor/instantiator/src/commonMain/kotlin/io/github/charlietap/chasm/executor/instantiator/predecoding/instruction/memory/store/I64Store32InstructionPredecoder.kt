package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store32Dispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Store32

internal fun I64Store32InstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.I64Store32,
): Result<DispatchableInstruction, ModuleTrapError> =
    I64Store32InstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I64Store32Dispatcher,
    )

internal inline fun I64Store32InstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.I64Store32,
    crossinline dispatcher: Dispatcher<I64Store32>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)

    dispatcher(I64Store32(memory, instruction.memArg))
}
