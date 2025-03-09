package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32TruncExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32TruncDispatcher(
    instruction: FusedNumericInstruction.F32Trunc,
) = F32TruncDispatcher(
    instruction = instruction,
    executor = ::F32TruncExecutor,
)

internal inline fun F32TruncDispatcher(
    instruction: FusedNumericInstruction.F32Trunc,
    crossinline executor: Executor<FusedNumericInstruction.F32Trunc>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
