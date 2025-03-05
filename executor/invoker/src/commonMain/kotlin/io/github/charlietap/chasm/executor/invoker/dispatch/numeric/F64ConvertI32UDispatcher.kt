package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64ConvertI32UDispatcher(
    instruction: NumericInstruction.F64ConvertI32U,
) = F64ConvertI32UDispatcher(
    instruction = instruction,
    executor = ::F64ConvertI32UExecutor,
)

internal inline fun F64ConvertI32UDispatcher(
    instruction: NumericInstruction.F64ConvertI32U,
    crossinline executor: Executor<NumericInstruction.F64ConvertI32U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
