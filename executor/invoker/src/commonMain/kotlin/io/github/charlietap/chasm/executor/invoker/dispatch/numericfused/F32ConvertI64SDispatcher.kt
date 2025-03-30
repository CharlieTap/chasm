package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32ConvertI64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32ConvertI64SDispatcher(
    instruction: FusedNumericInstruction.F32ConvertI64S,
) = F32ConvertI64SDispatcher(
    instruction = instruction,
    executor = ::F32ConvertI64SExecutor,
)

internal inline fun F32ConvertI64SDispatcher(
    instruction: FusedNumericInstruction.F32ConvertI64S,
    crossinline executor: Executor<FusedNumericInstruction.F32ConvertI64S>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
