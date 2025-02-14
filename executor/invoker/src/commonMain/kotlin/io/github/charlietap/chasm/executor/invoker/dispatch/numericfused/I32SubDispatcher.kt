package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32SubExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

fun I32SubDispatcher(
    instruction: FusedNumericInstruction.I32Sub,
) = I32SubDispatcher(
    instruction = instruction,
    executor = ::I32SubExecutor,
)

internal inline fun I32SubDispatcher(
    instruction: FusedNumericInstruction.I32Sub,
    crossinline executor: Executor<FusedNumericInstruction.I32Sub>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
