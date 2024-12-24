package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32StoreDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.F32Store

internal fun F32StoreInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.F32Store,
): Result<DispatchableInstruction, ModuleTrapError> =
    F32StoreInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::F32StoreDispatcher,
    )

internal inline fun F32StoreInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.F32Store,
    crossinline dispatcher: Dispatcher<F32Store>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress).bind()

    dispatcher(F32Store(memory, instruction.memArg))
}
