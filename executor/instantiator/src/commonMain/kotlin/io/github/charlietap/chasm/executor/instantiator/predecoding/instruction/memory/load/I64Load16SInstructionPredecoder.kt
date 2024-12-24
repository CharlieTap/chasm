package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load16SDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Load16S

internal fun I64Load16SInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.I64Load16S,
): Result<DispatchableInstruction, ModuleTrapError> =
    I64Load16SInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I64Load16SDispatcher,
    )

internal inline fun I64Load16SInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.I64Load16S,
    crossinline dispatcher: Dispatcher<I64Load16S>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress).bind()

    dispatcher(I64Load16S(memory, instruction.memArg))
}
