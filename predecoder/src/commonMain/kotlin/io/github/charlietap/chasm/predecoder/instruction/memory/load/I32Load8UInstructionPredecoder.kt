package io.github.charlietap.chasm.predecoder.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load8UDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.I32Load8U
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.predecoder.instruction.MemArgPredecoder

internal fun I32Load8UInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Load.I32Load8U,
): Result<DispatchableInstruction, ModuleTrapError> =
    I32Load8UInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I32Load8UDispatcher,
        memArgPredecoder = ::MemArgPredecoder,
    )

internal inline fun I32Load8UInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Load.I32Load8U,
    crossinline dispatcher: Dispatcher<I32Load8U>,
    crossinline memArgPredecoder: MemArgPredecoder,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)

    dispatcher(I32Load8U(memory, memArg))
}
