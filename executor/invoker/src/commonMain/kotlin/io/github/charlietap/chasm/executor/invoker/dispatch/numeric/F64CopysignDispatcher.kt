package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64CopysignExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64CopysignDispatcher(
    instruction: NumericInstruction.F64Copysign,
) = F64CopysignDispatcher(
    instruction = instruction,
    executor = ::F64CopysignExecutor,
)

internal inline fun F64CopysignDispatcher(
    instruction: NumericInstruction.F64Copysign,
    crossinline executor: Executor<NumericInstruction.F64Copysign>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
