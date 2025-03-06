package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64FloorExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64FloorDispatcher(
    instruction: FusedNumericInstruction.F64Floor,
) = F64FloorDispatcher(
    instruction = instruction,
    executor = ::F64FloorExecutor,
)

internal inline fun F64FloorDispatcher(
    instruction: FusedNumericInstruction.F64Floor,
    crossinline executor: Executor<FusedNumericInstruction.F64Floor>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
