package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend8SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64Extend8SDispatcher(
    instruction: NumericInstruction.I64Extend8S,
) = I64Extend8SDispatcher(
    instruction = instruction,
    executor = ::I64Extend8SExecutor,
)

internal inline fun I64Extend8SDispatcher(
    instruction: NumericInstruction.I64Extend8S,
    crossinline executor: Executor<NumericInstruction.I64Extend8S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
