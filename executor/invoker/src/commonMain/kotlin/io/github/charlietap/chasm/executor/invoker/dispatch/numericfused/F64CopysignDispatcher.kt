package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64CopysignExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64CopysignDispatcher(
    instruction: FusedNumericInstruction.F64Copysign,
) = F64CopysignDispatcher(
    instruction = instruction,
    executor = ::F64CopysignExecutor,
)

internal inline fun F64CopysignDispatcher(
    instruction: FusedNumericInstruction.F64Copysign,
    crossinline executor: Executor<FusedNumericInstruction.F64Copysign>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
