package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64ClzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ClzDispatcher(
    instruction: NumericInstruction.I64Clz,
) = I64ClzDispatcher(
    instruction = instruction,
    executor = ::I64ClzExecutor,
)

internal inline fun I64ClzDispatcher(
    instruction: NumericInstruction.I64Clz,
    crossinline executor: Executor<NumericInstruction.I64Clz>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
