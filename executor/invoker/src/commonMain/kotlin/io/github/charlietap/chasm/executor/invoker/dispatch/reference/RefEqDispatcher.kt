package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefEqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefEqDispatcher(
    instruction: ReferenceInstruction.RefEq,
) = RefEqDispatcher(
    instruction = instruction,
    executor = ::RefEqExecutor,
)

internal inline fun RefEqDispatcher(
    instruction: ReferenceInstruction.RefEq,
    crossinline executor: Executor<ReferenceInstruction.RefEq>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
