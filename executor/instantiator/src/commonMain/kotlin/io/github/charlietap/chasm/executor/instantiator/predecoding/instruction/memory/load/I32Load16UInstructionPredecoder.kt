package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load16UDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Load16U

internal fun I32Load16UInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.I32Load16U,
): Result<DispatchableInstruction, ModuleTrapError> =
    I32Load16UInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I32Load16UDispatcher,
    )

internal inline fun I32Load16UInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.I32Load16U,
    crossinline dispatcher: Dispatcher<I32Load16U>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)

    dispatcher(I32Load16U(memory, instruction.memArg))
}
