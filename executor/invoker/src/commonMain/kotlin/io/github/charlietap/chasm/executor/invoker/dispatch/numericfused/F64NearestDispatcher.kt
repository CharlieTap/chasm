package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64NearestExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64NearestDispatcher(
    instruction: FusedNumericInstruction.F64Nearest,
) = F64NearestDispatcher(
    instruction = instruction,
    executor = ::F64NearestExecutor,
)

internal inline fun F64NearestDispatcher(
    instruction: FusedNumericInstruction.F64Nearest,
    crossinline executor: Executor<FusedNumericInstruction.F64Nearest>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
