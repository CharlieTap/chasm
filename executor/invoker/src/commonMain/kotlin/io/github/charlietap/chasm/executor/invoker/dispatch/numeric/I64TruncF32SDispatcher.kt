package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64TruncF32SDispatcher(
    instruction: NumericInstruction.I64TruncF32S,
) = I64TruncF32SDispatcher(
    instruction = instruction,
    executor = ::I64TruncF32SExecutor,
)

internal inline fun I64TruncF32SDispatcher(
    instruction: NumericInstruction.I64TruncF32S,
    crossinline executor: Executor<NumericInstruction.I64TruncF32S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
