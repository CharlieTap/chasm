package io.github.charlietap.chasm.executor.invoker.dispatch.referencefused

import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefNullExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction

fun RefNullDispatcher(
    instruction: FusedReferenceInstruction.RefNull,
) = RefNullDispatcher(
    instruction = instruction,
    executor = ::RefNullExecutor,
)

internal inline fun RefNullDispatcher(
    instruction: FusedReferenceInstruction.RefNull,
    crossinline executor: Executor<FusedReferenceInstruction.RefNull>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
