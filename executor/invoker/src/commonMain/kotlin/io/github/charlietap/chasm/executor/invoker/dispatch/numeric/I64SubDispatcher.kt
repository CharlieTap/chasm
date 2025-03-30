package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64SubExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64SubDispatcher(
    instruction: NumericInstruction.I64Sub,
) = I64SubDispatcher(
    instruction = instruction,
    executor = ::I64SubExecutor,
)

internal inline fun I64SubDispatcher(
    instruction: NumericInstruction.I64Sub,
    crossinline executor: Executor<NumericInstruction.I64Sub>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
