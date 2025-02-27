package io.github.charlietap.chasm.executor.invoker.dispatch.referencefused

import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefIsNullExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedReferenceInstruction

fun RefIsNullDispatcher(
    instruction: FusedReferenceInstruction.RefIsNull,
) = RefIsNullDispatcher(
    instruction = instruction,
    executor = ::RefIsNullExecutor,
)

internal inline fun RefIsNullDispatcher(
    instruction: FusedReferenceInstruction.RefIsNull,
    crossinline executor: Executor<FusedReferenceInstruction.RefIsNull>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
