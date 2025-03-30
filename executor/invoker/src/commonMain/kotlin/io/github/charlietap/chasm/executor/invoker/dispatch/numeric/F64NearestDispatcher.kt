package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64NearestExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64NearestDispatcher(
    instruction: NumericInstruction.F64Nearest,
) = F64NearestDispatcher(
    instruction = instruction,
    executor = ::F64NearestExecutor,
)

internal inline fun F64NearestDispatcher(
    instruction: NumericInstruction.F64Nearest,
    crossinline executor: Executor<NumericInstruction.F64Nearest>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
