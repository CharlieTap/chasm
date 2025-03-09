package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64AndExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64AndDispatcher(
    instruction: NumericInstruction.I64And,
) = I64AndDispatcher(
    instruction = instruction,
    executor = ::I64AndExecutor,
)

internal inline fun I64AndDispatcher(
    instruction: NumericInstruction.I64And,
    crossinline executor: Executor<NumericInstruction.I64And>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
