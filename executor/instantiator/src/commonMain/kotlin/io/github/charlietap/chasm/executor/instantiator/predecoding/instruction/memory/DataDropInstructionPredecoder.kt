package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.DataDropDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.dataAddress
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction.DataDrop

internal fun DataDropInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.DataDrop,
): Result<DispatchableInstruction, ModuleTrapError> =
    DataDropInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::DataDropDispatcher,
    )

internal inline fun DataDropInstructionPredecoder(
    context: InstantiationContext,
    instruction: MemoryInstruction.DataDrop,
    crossinline dispatcher: Dispatcher<DataDrop>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val dataAddress = context.instance?.dataAddress(instruction.dataIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val data = context.store.data(dataAddress)

    dispatcher(DataDrop(data))
}
