package io.github.charlietap.chasm.executor.invoker.dispatch.referencefused

import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefEqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction

fun RefEqDispatcher(
    instruction: FusedReferenceInstruction.RefEq,
) = RefEqDispatcher(
    instruction = instruction,
    executor = ::RefEqExecutor,
)

internal inline fun RefEqDispatcher(
    instruction: FusedReferenceInstruction.RefEq,
    crossinline executor: Executor<FusedReferenceInstruction.RefEq>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
