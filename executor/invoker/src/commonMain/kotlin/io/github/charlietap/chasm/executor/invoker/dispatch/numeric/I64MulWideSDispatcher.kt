package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64MulWideSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64MulWideSDispatcher(
    instruction: NumericInstruction.I64MulWideS,
) = I64MulWideSDispatcher(
    instruction = instruction,
    executor = ::I64MulWideSExecutor,
)

internal inline fun I64MulWideSDispatcher(
    instruction: NumericInstruction.I64MulWideS,
    crossinline executor: Executor<NumericInstruction.I64MulWideS>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
