package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RemSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32RemSDispatcher(
    instruction: NumericInstruction.I32RemS,
) = I32RemSDispatcher(
    instruction = instruction,
    executor = ::I32RemSExecutor,
)

internal inline fun I32RemSDispatcher(
    instruction: NumericInstruction.I32RemS,
    crossinline executor: Executor<NumericInstruction.I32RemS>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
