package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I64Store32Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64Store32Dispatcher(
    instruction: FusedMemoryInstruction.I64Store32,
) = I64Store32Dispatcher(
    instruction = instruction,
    executor = ::I64Store32Executor,
)

internal inline fun I64Store32Dispatcher(
    instruction: FusedMemoryInstruction.I64Store32,
    crossinline executor: Executor<FusedMemoryInstruction.I64Store32>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
