package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncF32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32TruncF32UDispatcher(
    instruction: FusedNumericInstruction.I32TruncF32U,
) = I32TruncF32UDispatcher(
    instruction = instruction,
    executor = ::I32TruncF32UExecutor,
)

internal inline fun I32TruncF32UDispatcher(
    instruction: FusedNumericInstruction.I32TruncF32U,
    crossinline executor: Executor<FusedNumericInstruction.I32TruncF32U>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
