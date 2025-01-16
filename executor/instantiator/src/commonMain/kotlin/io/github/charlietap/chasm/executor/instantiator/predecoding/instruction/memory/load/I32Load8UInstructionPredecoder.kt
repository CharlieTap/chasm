package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load8UDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Load8U

internal fun I32Load8UInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.I32Load8U,
): Result<DispatchableInstruction, ModuleTrapError> =
    I32Load8UInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I32Load8UDispatcher,
    )

internal inline fun I32Load8UInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.I32Load8U,
    crossinline dispatcher: Dispatcher<I32Load8U>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)

    dispatcher(I32Load8U(memory, instruction.memArg))
}
