package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64LeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64LeDispatcher(
    instruction: FusedNumericInstruction.F64Le,
) = F64LeDispatcher(
    instruction = instruction,
    executor = ::F64LeExecutor,
)

internal inline fun F64LeDispatcher(
    instruction: FusedNumericInstruction.F64Le,
    crossinline executor: Executor<FusedNumericInstruction.F64Le>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
