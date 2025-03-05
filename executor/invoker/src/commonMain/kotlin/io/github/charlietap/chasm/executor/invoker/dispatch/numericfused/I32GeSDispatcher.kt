package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32GeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32GeSDispatcher(
    instruction: FusedNumericInstruction.I32GeS,
) = I32GeSDispatcher(
    instruction = instruction,
    executor = ::I32GeSExecutor,
)

internal inline fun I32GeSDispatcher(
    instruction: FusedNumericInstruction.I32GeS,
    crossinline executor: Executor<FusedNumericInstruction.I32GeS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
