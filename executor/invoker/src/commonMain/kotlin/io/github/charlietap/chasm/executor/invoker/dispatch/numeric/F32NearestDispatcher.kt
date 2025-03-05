package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32NearestExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32NearestDispatcher(
    instruction: NumericInstruction.F32Nearest,
) = F32NearestDispatcher(
    instruction = instruction,
    executor = ::F32NearestExecutor,
)

internal inline fun F32NearestDispatcher(
    instruction: NumericInstruction.F32Nearest,
    crossinline executor: Executor<NumericInstruction.F32Nearest>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
