package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32TruncExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32TruncDispatcher(
    instruction: NumericInstruction.F32Trunc,
) = F32TruncDispatcher(
    instruction = instruction,
    executor = ::F32TruncExecutor,
)

internal inline fun F32TruncDispatcher(
    instruction: NumericInstruction.F32Trunc,
    crossinline executor: Executor<NumericInstruction.F32Trunc>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
