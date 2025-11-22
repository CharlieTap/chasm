package io.github.charlietap.chasm.predecoder.instruction.memory.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32StoreDispatcher
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.predecoder.instruction.MemArgPredecoder
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction.F32Store

internal fun F32StoreInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Store.F32.F32Store,
): Result<DispatchableInstruction, ModuleTrapError> =
    F32StoreInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::F32StoreDispatcher,
        memArgPredecoder = ::MemArgPredecoder,
    )

internal inline fun F32StoreInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.Store.F32.F32Store,
    crossinline dispatcher: Dispatcher<F32Store>,
    crossinline memArgPredecoder: MemArgPredecoder,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)

    dispatcher(F32Store(memory, memArg))
}
