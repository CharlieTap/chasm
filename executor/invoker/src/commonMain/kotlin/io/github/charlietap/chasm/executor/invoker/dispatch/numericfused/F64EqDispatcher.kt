package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64EqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64EqDispatcher(
    instruction: FusedNumericInstruction.F64Eq,
) = F64EqDispatcher(
    instruction = instruction,
    executor = ::F64EqExecutor,
)

internal inline fun F64EqDispatcher(
    instruction: FusedNumericInstruction.F64Eq,
    crossinline executor: Executor<FusedNumericInstruction.F64Eq>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
