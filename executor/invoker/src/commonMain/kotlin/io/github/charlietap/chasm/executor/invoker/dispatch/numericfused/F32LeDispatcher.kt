package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32LeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32LeDispatcher(
    instruction: FusedNumericInstruction.F32Le,
) = F32LeDispatcher(
    instruction = instruction,
    executor = ::F32LeExecutor,
)

internal inline fun F32LeDispatcher(
    instruction: FusedNumericInstruction.F32Le,
    crossinline executor: Executor<FusedNumericInstruction.F32Le>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
