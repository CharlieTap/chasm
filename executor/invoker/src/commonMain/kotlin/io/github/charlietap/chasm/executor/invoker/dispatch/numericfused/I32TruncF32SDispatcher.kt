package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncF32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32TruncF32SDispatcher(
    instruction: FusedNumericInstruction.I32TruncF32S,
) = I32TruncF32SDispatcher(
    instruction = instruction,
    executor = ::I32TruncF32SExecutor,
)

internal inline fun I32TruncF32SDispatcher(
    instruction: FusedNumericInstruction.I32TruncF32S,
    crossinline executor: Executor<FusedNumericInstruction.I32TruncF32S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
