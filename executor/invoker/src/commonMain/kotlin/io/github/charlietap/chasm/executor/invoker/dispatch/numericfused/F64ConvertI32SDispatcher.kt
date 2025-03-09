package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64ConvertI32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64ConvertI32SDispatcher(
    instruction: FusedNumericInstruction.F64ConvertI32S,
) = F64ConvertI32SDispatcher(
    instruction = instruction,
    executor = ::F64ConvertI32SExecutor,
)

internal inline fun F64ConvertI32SDispatcher(
    instruction: FusedNumericInstruction.F64ConvertI32S,
    crossinline executor: Executor<FusedNumericInstruction.F64ConvertI32S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
