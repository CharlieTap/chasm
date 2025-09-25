package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64Sub128Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128,
) = I64Sub128Dispatcher(
    instruction = instruction,
    executor = ::I64Sub128Executor,
)

internal inline fun I64Sub128Dispatcher(
    instruction: NumericInstruction.I64Sub128,
    crossinline executor: Executor<NumericInstruction.I64Sub128>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
