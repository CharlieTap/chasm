package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RemSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64RemSDispatcher(
    instruction: NumericInstruction.I64RemS,
) = I64RemSDispatcher(
    instruction = instruction,
    executor = ::I64RemSExecutor,
)

internal inline fun I64RemSDispatcher(
    instruction: NumericInstruction.I64RemS,
    crossinline executor: Executor<NumericInstruction.I64RemS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
