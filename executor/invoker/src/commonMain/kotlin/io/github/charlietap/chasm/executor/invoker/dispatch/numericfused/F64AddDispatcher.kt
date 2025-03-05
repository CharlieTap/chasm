package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64AddExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64AddDispatcher(
    instruction: FusedNumericInstruction.F64Add,
) = F64AddDispatcher(
    instruction = instruction,
    executor = ::F64AddExecutor,
)

internal inline fun F64AddDispatcher(
    instruction: FusedNumericInstruction.F64Add,
    crossinline executor: Executor<FusedNumericInstruction.F64Add>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
