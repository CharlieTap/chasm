package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64CeilExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64CeilDispatcher(
    instruction: NumericInstruction.F64Ceil,
) = F64CeilDispatcher(
    instruction = instruction,
    executor = ::F64CeilExecutor,
)

internal inline fun F64CeilDispatcher(
    instruction: NumericInstruction.F64Ceil,
    crossinline executor: Executor<NumericInstruction.F64Ceil>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
