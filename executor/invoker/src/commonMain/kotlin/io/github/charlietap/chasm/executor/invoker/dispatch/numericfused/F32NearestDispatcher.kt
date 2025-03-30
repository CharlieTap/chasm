package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32NearestExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32NearestDispatcher(
    instruction: FusedNumericInstruction.F32Nearest,
) = F32NearestDispatcher(
    instruction = instruction,
    executor = ::F32NearestExecutor,
)

internal inline fun F32NearestDispatcher(
    instruction: FusedNumericInstruction.F32Nearest,
    crossinline executor: Executor<FusedNumericInstruction.F32Nearest>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
