package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64RemSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64RemSDispatcher(
    instruction: FusedNumericInstruction.I64RemS,
) = I64RemSDispatcher(
    instruction = instruction,
    executor = ::I64RemSExecutor,
)

internal inline fun I64RemSDispatcher(
    instruction: FusedNumericInstruction.I64RemS,
    crossinline executor: Executor<FusedNumericInstruction.I64RemS>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
