package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShrSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ShrSDispatcher(
    instruction: NumericInstruction.I32ShrS,
) = I32ShrSDispatcher(
    instruction = instruction,
    executor = ::I32ShrSExecutor,
)

internal inline fun I32ShrSDispatcher(
    instruction: NumericInstruction.I32ShrS,
    crossinline executor: Executor<NumericInstruction.I32ShrS>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
