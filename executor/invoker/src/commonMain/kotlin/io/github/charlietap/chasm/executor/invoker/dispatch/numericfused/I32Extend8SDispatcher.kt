package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I32Extend8SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32Extend8SDispatcher(
    instruction: FusedNumericInstruction.I32Extend8S,
) = I32Extend8SDispatcher(
    instruction = instruction,
    executor = ::I32Extend8SExecutor,
)

internal inline fun I32Extend8SDispatcher(
    instruction: FusedNumericInstruction.I32Extend8S,
    crossinline executor: Executor<FusedNumericInstruction.I32Extend8S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
