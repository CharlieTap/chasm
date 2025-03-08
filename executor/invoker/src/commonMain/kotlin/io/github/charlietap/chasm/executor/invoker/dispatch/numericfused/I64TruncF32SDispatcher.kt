package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncF32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64TruncF32SDispatcher(
    instruction: FusedNumericInstruction.I64TruncF32S,
) = I64TruncF32SDispatcher(
    instruction = instruction,
    executor = ::I64TruncF32SExecutor,
)

internal inline fun I64TruncF32SDispatcher(
    instruction: FusedNumericInstruction.I64TruncF32S,
    crossinline executor: Executor<FusedNumericInstruction.I64TruncF32S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
