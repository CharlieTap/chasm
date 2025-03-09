package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RemSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32RemSDispatcher(
    instruction: FusedNumericInstruction.I32RemS,
) = I32RemSDispatcher(
    instruction = instruction,
    executor = ::I32RemSExecutor,
)

internal inline fun I32RemSDispatcher(
    instruction: FusedNumericInstruction.I32RemS,
    crossinline executor: Executor<FusedNumericInstruction.I32RemS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
