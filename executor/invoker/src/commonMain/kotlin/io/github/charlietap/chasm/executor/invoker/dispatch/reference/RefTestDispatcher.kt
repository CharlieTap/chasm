package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefTestExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction

fun RefTestDispatcher(
    instruction: ReferenceInstruction.RefTest,
) = RefTestDispatcher(
    instruction = instruction,
    executor = ::RefTestExecutor,
)

internal inline fun RefTestDispatcher(
    instruction: ReferenceInstruction.RefTest,
    crossinline executor: Executor<ReferenceInstruction.RefTest>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
