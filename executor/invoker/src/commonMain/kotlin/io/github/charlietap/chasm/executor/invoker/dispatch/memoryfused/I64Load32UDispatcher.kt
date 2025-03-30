package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64Load32UDispatcher(
    instruction: FusedMemoryInstruction.I64Load32U,
) = I64Load32UDispatcher(
    instruction = instruction,
    executor = ::I64Load32UExecutor,
)

internal inline fun I64Load32UDispatcher(
    instruction: FusedMemoryInstruction.I64Load32U,
    crossinline executor: Executor<FusedMemoryInstruction.I64Load32U>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
