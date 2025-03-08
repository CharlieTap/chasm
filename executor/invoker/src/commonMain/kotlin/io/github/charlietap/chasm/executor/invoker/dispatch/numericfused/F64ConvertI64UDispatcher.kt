package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64ConvertI64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64ConvertI64UDispatcher(
    instruction: FusedNumericInstruction.F64ConvertI64U,
) = F64ConvertI64UDispatcher(
    instruction = instruction,
    executor = ::F64ConvertI64UExecutor,
)

internal inline fun F64ConvertI64UDispatcher(
    instruction: FusedNumericInstruction.F64ConvertI64U,
    crossinline executor: Executor<FusedNumericInstruction.F64ConvertI64U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
