package io.github.charlietap.chasm.predecoder.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32LoadDispatcher
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.predecoder.instruction.MemArgPredecoder
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction.F32Load

internal fun F32LoadInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Load.F32.F32Load,
): Result<DispatchableInstruction, ModuleTrapError> =
    F32LoadInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::F32LoadDispatcher,
        memArgPredecoder = ::MemArgPredecoder,
    )

internal inline fun F32LoadInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Load.F32.F32Load,
    crossinline dispatcher: Dispatcher<F32Load>,
    crossinline memArgPredecoder: MemArgPredecoder,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(instruction.memoryIndex).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)

    dispatcher(F32Load(memory, memArg))
}
