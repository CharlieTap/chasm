package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64ConvertI32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64ConvertI32UDispatcher(
    instruction: FusedNumericInstruction.F64ConvertI32U,
) = F64ConvertI32UDispatcher(
    instruction = instruction,
    executor = ::F64ConvertI32UExecutor,
)

internal inline fun F64ConvertI32UDispatcher(
    instruction: FusedNumericInstruction.F64ConvertI32U,
    crossinline executor: Executor<FusedNumericInstruction.F64ConvertI32U>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
