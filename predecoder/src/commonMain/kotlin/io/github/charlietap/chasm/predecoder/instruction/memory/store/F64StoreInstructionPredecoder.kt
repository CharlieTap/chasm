package io.github.charlietap.chasm.predecoder.instruction.memory.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F64StoreDispatcher
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.predecoder.instruction.MemArgPredecoder
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction.F64Store

internal fun F64StoreInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Store.F64Store,
): Result<DispatchableInstruction, ModuleTrapError> =
    F64StoreInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::F64StoreDispatcher,
        memArgPredecoder = ::MemArgPredecoder,
    )

internal inline fun F64StoreInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Store.F64Store,
    crossinline dispatcher: Dispatcher<F64Store>,
    crossinline memArgPredecoder: MemArgPredecoder,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)

    dispatcher(F64Store(memory, memArg))
}
