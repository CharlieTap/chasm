package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64AddExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64AddDispatcher(
    instruction: NumericInstruction.I64Add,
) = I64AddDispatcher(
    instruction = instruction,
    executor = ::I64AddExecutor,
)

internal inline fun I64AddDispatcher(
    instruction: NumericInstruction.I64Add,
    crossinline executor: Executor<NumericInstruction.I64Add>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
