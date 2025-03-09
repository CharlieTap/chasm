package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32CopysignExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32CopysignDispatcher(
    instruction: FusedNumericInstruction.F32Copysign,
) = F32CopysignDispatcher(
    instruction = instruction,
    executor = ::F32CopysignExecutor,
)

internal inline fun F32CopysignDispatcher(
    instruction: FusedNumericInstruction.F32Copysign,
    crossinline executor: Executor<FusedNumericInstruction.F32Copysign>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
