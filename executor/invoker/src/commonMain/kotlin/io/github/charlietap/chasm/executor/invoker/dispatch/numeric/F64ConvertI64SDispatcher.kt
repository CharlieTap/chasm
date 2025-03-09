package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64ConvertI64SDispatcher(
    instruction: NumericInstruction.F64ConvertI64S,
) = F64ConvertI64SDispatcher(
    instruction = instruction,
    executor = ::F64ConvertI64SExecutor,
)

internal inline fun F64ConvertI64SDispatcher(
    instruction: NumericInstruction.F64ConvertI64S,
    crossinline executor: Executor<NumericInstruction.F64ConvertI64S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
