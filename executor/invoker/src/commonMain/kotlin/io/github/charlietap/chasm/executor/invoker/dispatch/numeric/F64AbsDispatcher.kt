package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64AbsExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64AbsDispatcher(
    instruction: NumericInstruction.F64Abs,
) = F64AbsDispatcher(
    instruction = instruction,
    executor = ::F64AbsExecutor,
)

internal inline fun F64AbsDispatcher(
    instruction: NumericInstruction.F64Abs,
    crossinline executor: Executor<NumericInstruction.F64Abs>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
