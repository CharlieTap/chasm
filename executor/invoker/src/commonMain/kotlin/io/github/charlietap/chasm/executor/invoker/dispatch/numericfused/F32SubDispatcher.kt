package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32SubExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32SubDispatcher(
    instruction: FusedNumericInstruction.F32Sub,
) = F32SubDispatcher(
    instruction = instruction,
    executor = ::F32SubExecutor,
)

internal inline fun F32SubDispatcher(
    instruction: FusedNumericInstruction.F32Sub,
    crossinline executor: Executor<FusedNumericInstruction.F32Sub>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
