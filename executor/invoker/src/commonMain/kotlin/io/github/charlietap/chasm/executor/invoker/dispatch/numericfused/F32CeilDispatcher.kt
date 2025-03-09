package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32CeilExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32CeilDispatcher(
    instruction: FusedNumericInstruction.F32Ceil,
) = F32CeilDispatcher(
    instruction = instruction,
    executor = ::F32CeilExecutor,
)

internal inline fun F32CeilDispatcher(
    instruction: FusedNumericInstruction.F32Ceil,
    crossinline executor: Executor<FusedNumericInstruction.F32Ceil>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
