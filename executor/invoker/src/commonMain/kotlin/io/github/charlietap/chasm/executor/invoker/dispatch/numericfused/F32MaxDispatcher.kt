package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32MaxExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32MaxDispatcher(
    instruction: FusedNumericInstruction.F32Max,
) = F32MaxDispatcher(
    instruction = instruction,
    executor = ::F32MaxExecutor,
)

internal inline fun F32MaxDispatcher(
    instruction: FusedNumericInstruction.F32Max,
    crossinline executor: Executor<FusedNumericInstruction.F32Max>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
