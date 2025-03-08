package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64ConvertI64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64ConvertI64SDispatcher(
    instruction: FusedNumericInstruction.F64ConvertI64S,
) = F64ConvertI64SDispatcher(
    instruction = instruction,
    executor = ::F64ConvertI64SExecutor,
)

internal inline fun F64ConvertI64SDispatcher(
    instruction: FusedNumericInstruction.F64ConvertI64S,
    crossinline executor: Executor<FusedNumericInstruction.F64ConvertI64S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
