package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64ConvertI32SDispatcher(
    instruction: NumericInstruction.F64ConvertI32S,
) = F64ConvertI32SDispatcher(
    instruction = instruction,
    executor = ::F64ConvertI32SExecutor,
)

internal inline fun F64ConvertI32SDispatcher(
    instruction: NumericInstruction.F64ConvertI32S,
    crossinline executor: Executor<NumericInstruction.F64ConvertI32S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
