package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.memoryAddress
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.MemArgPredecoder
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store8Dispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I64Store8
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction

internal fun I64Store8InstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.I64Store8,
): Result<DispatchableInstruction, ModuleTrapError> =
    I64Store8InstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I64Store8Dispatcher,
        memArgPredecoder = ::MemArgPredecoder,
    )

internal inline fun I64Store8InstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.I64Store8,
    crossinline dispatcher: Dispatcher<I64Store8>,
    crossinline memArgPredecoder: MemArgPredecoder,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)

    dispatcher(I64Store8(memory, memArg))
}
