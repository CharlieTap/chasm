package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MulExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F64MulDispatcher(
    instruction: NumericInstruction.F64Mul,
) = F64MulDispatcher(
    instruction = instruction,
    executor = ::F64MulExecutor,
)

internal inline fun F64MulDispatcher(
    instruction: NumericInstruction.F64Mul,
    crossinline executor: Executor<NumericInstruction.F64Mul>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
