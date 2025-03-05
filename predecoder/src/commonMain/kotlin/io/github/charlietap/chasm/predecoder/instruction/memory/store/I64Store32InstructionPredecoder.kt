package io.github.charlietap.chasm.predecoder.instruction.memory.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store32Dispatcher
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.predecoder.instruction.MemArgPredecoder
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction.I64Store32

internal fun I64Store32InstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Store.I64Store32,
): Result<DispatchableInstruction, ModuleTrapError> =
    I64Store32InstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I64Store32Dispatcher,
        memArgPredecoder = ::MemArgPredecoder,
    )

internal inline fun I64Store32InstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Store.I64Store32,
    crossinline dispatcher: Dispatcher<I64Store32>,
    crossinline memArgPredecoder: MemArgPredecoder,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)

    dispatcher(I64Store32(memory, memArg))
}
