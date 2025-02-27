package io.github.charlietap.chasm.executor.invoker.dispatch.referencefused

import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefEqExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedReferenceInstruction

fun RefEqDispatcher(
    instruction: FusedReferenceInstruction.RefEq,
) = RefEqDispatcher(
    instruction = instruction,
    executor = ::RefEqExecutor,
)

internal inline fun RefEqDispatcher(
    instruction: FusedReferenceInstruction.RefEq,
    crossinline executor: Executor<FusedReferenceInstruction.RefEq>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
