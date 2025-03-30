package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64FloorExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64FloorDispatcher(
    instruction: NumericInstruction.F64Floor,
) = F64FloorDispatcher(
    instruction = instruction,
    executor = ::F64FloorExecutor,
)

internal inline fun F64FloorDispatcher(
    instruction: NumericInstruction.F64Floor,
    crossinline executor: Executor<NumericInstruction.F64Floor>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
