package io.github.charlietap.chasm.predecoder.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.DataDropDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.DataDrop
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.dataAddress

internal fun DataDropInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.DataDrop,
): Result<DispatchableInstruction, ModuleTrapError> =
    DataDropInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::DataDropDispatcher,
    )

internal inline fun DataDropInstructionPredecoder(
    context: PredecodingContext,
    instruction: MemoryInstruction.DataDrop,
    crossinline dispatcher: Dispatcher<DataDrop>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val dataAddress = context.instance.dataAddress(instruction.dataIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val data = context.store.data(dataAddress)

    dispatcher(DataDrop(data))
}
