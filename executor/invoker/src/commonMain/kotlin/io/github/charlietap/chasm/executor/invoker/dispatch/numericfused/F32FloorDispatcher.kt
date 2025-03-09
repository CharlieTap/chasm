package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32FloorExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32FloorDispatcher(
    instruction: FusedNumericInstruction.F32Floor,
) = F32FloorDispatcher(
    instruction = instruction,
    executor = ::F32FloorExecutor,
)

internal inline fun F32FloorDispatcher(
    instruction: FusedNumericInstruction.F32Floor,
    crossinline executor: Executor<FusedNumericInstruction.F32Floor>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
