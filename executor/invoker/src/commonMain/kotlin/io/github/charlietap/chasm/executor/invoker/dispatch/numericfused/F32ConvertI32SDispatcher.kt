package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32ConvertI32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32ConvertI32SDispatcher(
    instruction: FusedNumericInstruction.F32ConvertI32S,
) = F32ConvertI32SDispatcher(
    instruction = instruction,
    executor = ::F32ConvertI32SExecutor,
)

internal inline fun F32ConvertI32SDispatcher(
    instruction: FusedNumericInstruction.F32ConvertI32S,
    crossinline executor: Executor<FusedNumericInstruction.F32ConvertI32S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
