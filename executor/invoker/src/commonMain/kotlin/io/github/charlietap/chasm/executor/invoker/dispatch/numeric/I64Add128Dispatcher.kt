package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64Add128Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128,
) = I64Add128Dispatcher(
    instruction = instruction,
    executor = ::I64Add128Executor,
)

internal inline fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128,
    crossinline executor: Executor<NumericInstruction.I64Add128>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
