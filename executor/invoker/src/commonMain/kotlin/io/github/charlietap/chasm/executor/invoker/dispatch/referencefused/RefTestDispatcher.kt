package io.github.charlietap.chasm.executor.invoker.dispatch.referencefused

import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefTestExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction

fun RefTestDispatcher(
    instruction: FusedReferenceInstruction.RefTest,
) = RefTestDispatcher(
    instruction = instruction,
    executor = ::RefTestExecutor,
)

internal inline fun RefTestDispatcher(
    instruction: FusedReferenceInstruction.RefTest,
    crossinline executor: Executor<FusedReferenceInstruction.RefTest>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
