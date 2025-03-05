package io.github.charlietap.chasm.predecoder.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load32SDispatcher
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.predecoder.instruction.MemArgPredecoder
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction.I64Load32S

internal fun I64Load32SInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Load.I64Load32S,
): Result<DispatchableInstruction, ModuleTrapError> =
    I64Load32SInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I64Load32SDispatcher,
        memArgPredecoder = ::MemArgPredecoder,
    )

internal inline fun I64Load32SInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Load.I64Load32S,
    crossinline dispatcher: Dispatcher<I64Load32S>,
    crossinline memArgPredecoder: MemArgPredecoder,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)

    dispatcher(I64Load32S(memory, memArg))
}
