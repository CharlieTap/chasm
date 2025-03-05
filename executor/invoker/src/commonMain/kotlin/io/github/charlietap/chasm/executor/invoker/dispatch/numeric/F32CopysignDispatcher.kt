package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32CopysignExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32CopysignDispatcher(
    instruction: NumericInstruction.F32Copysign,
) = F32CopysignDispatcher(
    instruction = instruction,
    executor = ::F32CopysignExecutor,
)

internal inline fun F32CopysignDispatcher(
    instruction: NumericInstruction.F32Copysign,
    crossinline executor: Executor<NumericInstruction.F32Copysign>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
