package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefTestExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefTestDispatcher(
    instruction: ReferenceInstruction.RefTest,
) = RefTestDispatcher(
    instruction = instruction,
    executor = ::RefTestExecutor,
)

internal inline fun RefTestDispatcher(
    instruction: ReferenceInstruction.RefTest,
    crossinline executor: Executor<ReferenceInstruction.RefTest>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
