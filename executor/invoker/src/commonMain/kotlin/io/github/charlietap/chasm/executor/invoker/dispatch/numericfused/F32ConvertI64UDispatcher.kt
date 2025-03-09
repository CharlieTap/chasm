package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32ConvertI64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32ConvertI64UDispatcher(
    instruction: FusedNumericInstruction.F32ConvertI64U,
) = F32ConvertI64UDispatcher(
    instruction = instruction,
    executor = ::F32ConvertI64UExecutor,
)

internal inline fun F32ConvertI64UDispatcher(
    instruction: FusedNumericInstruction.F32ConvertI64U,
    crossinline executor: Executor<FusedNumericInstruction.F32ConvertI64U>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
