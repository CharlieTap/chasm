package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64AddExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64AddDispatcher(
    instruction: NumericInstruction.F64Add,
) = F64AddDispatcher(
    instruction = instruction,
    executor = ::F64AddExecutor,
)

internal inline fun F64AddDispatcher(
    instruction: NumericInstruction.F64Add,
    crossinline executor: Executor<NumericInstruction.F64Add>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
