package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64TruncExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64TruncDispatcher(
    instruction: NumericInstruction.F64Trunc,
) = F64TruncDispatcher(
    instruction = instruction,
    executor = ::F64TruncExecutor,
)

internal inline fun F64TruncDispatcher(
    instruction: NumericInstruction.F64Trunc,
    crossinline executor: Executor<NumericInstruction.F64Trunc>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
