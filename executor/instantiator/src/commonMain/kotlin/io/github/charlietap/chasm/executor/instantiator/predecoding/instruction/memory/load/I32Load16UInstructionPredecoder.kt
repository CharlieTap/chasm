package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.memoryAddress
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.MemArgPredecoder
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load16UDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Load16U
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction

internal fun I32Load16UInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.Load.I32Load16U,
): Result<DispatchableInstruction, ModuleTrapError> =
    I32Load16UInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I32Load16UDispatcher,
        memArgPredecoder = ::MemArgPredecoder,
    )

internal inline fun I32Load16UInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.Load.I32Load16U,
    crossinline dispatcher: Dispatcher<I32Load16U>,
    crossinline memArgPredecoder: MemArgPredecoder,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)

    dispatcher(I32Load16U(memory, memArg))
}
